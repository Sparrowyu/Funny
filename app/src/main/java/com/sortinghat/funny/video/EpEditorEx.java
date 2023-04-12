package com.sortinghat.funny.video;

import android.media.MediaExtractor;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import io.microshow.rxffmpeg.RxFFmpegInvoke;


public class EpEditorEx {

	private static final int DEFAULT_WIDTH = 480;//默认输出宽度
	private static final int DEFAULT_HEIGHT = 360;//默认输出高度

	public enum Format {
		MP3, MP4
	}

	public enum PTS {
		VIDEO, AUDIO, ALL
	}

	private EpEditorEx() {
	}

	/**
	 * 处理单个视频
	 *
	 * @param epVideo      需要处理的视频
	 * @param outputOption 输出选项配置
	 */
	public static void exec(EpVideo epVideo, OutputOption outputOption, RxFFmpegInvoke.IFFmpegListener listener) {
		boolean isFilter = false;
		ArrayList<EpDraw> epDraws = epVideo.getEpDraws();
		//开始处理
		CmdList cmd = new CmdList();
		cmd.append("ffmpeg");
		cmd.append("-y");
		if (epVideo.getVideoClip()) {
			cmd.append("-ss").append(epVideo.getClipStart()).append("-t").append(epVideo.getClipDuration()).append("-accurate_seek");
		}
		cmd.append("-i").append(epVideo.getVideoPath());
		//添加图片或者动图
		if (epDraws.size() > 0) {
			for (int i = 0; i < epDraws.size(); i++) {
				if (epDraws.get(i).isAnimation()) {
					cmd.append("-ignore_loop");
					cmd.append(0);
				}
				cmd.append("-i").append(epDraws.get(i).getPicPath());
			}
			cmd.append("-filter_complex");
			StringBuilder filter_complex = new StringBuilder();
			filter_complex.append("[0:v]").append(epVideo.getFilters() != null ? epVideo.getFilters() + "," : "")
					.append("scale=").append(outputOption.width == 0 ? "iw" : outputOption.width).append(":")
					.append(outputOption.height == 0 ? "ih" : outputOption.height)
					.append(outputOption.width == 0 ? "" : ",setdar=" + outputOption.getSar()).append("[outv0];");
			for (int i = 0; i < epDraws.size(); i++) {
				filter_complex.append("[").append(i + 1).append(":0]").append(epDraws.get(i).getPicFilter()).append("scale=").append(epDraws.get(i).getPicWidth()).append(":")
						.append(epDraws.get(i).getPicHeight()).append("[outv").append(i + 1).append("];");
			}
			for (int i = 0; i < epDraws.size(); i++) {
				if (i == 0) {
					filter_complex.append("[outv").append(i).append("]").append("[outv").append(i + 1).append("]");
				} else {
					filter_complex.append("[outo").append(i - 1).append("]").append("[outv").append(i + 1).append("]");
				}
				filter_complex.append("overlay=").append(epDraws.get(i).getPicX()).append(":").append(epDraws.get(i).getPicY())
						.append(epDraws.get(i).getTime());
				if (epDraws.get(i).isAnimation()) {
					filter_complex.append(":shortest=1");
				}
				if (i < epDraws.size() - 1) {
					filter_complex.append("[outo").append(i).append("];");
				}
			}
			cmd.append(filter_complex.toString());
			isFilter = true;
		} else {
			StringBuilder filter_complex = new StringBuilder();
			if (epVideo.getFilters() != null) {
				cmd.append("-filter_complex");
				filter_complex.append(epVideo.getFilters());
				isFilter = true;
			}
			//设置输出分辨率
			if (outputOption.width != 0) {
				if (epVideo.getFilters() != null) {
					filter_complex.append(",scale=").append(outputOption.width).append(":").append(outputOption.height)
							.append(",setdar=").append(outputOption.getSar());
				} else {
					cmd.append("-filter_complex");
					filter_complex.append("scale=").append(outputOption.width).append(":").append(outputOption.height)
							.append(",setdar=").append(outputOption.getSar());
					isFilter = true;
				}
			}
			if (!filter_complex.toString().equals("")) {
				cmd.append(filter_complex.toString());
			}
		}

		//输出选项
		cmd.append(outputOption.getOutputInfo().split(" "));
		if (!isFilter && outputOption.getOutputInfo().isEmpty()) {
			cmd.append("-vcodec");
			cmd.append("copy");
			cmd.append("-acodec");
			cmd.append("copy");
		} else {
			cmd.append("-preset");
			cmd.append("superfast");
		}
		cmd.append(outputOption.outPath);
		long duration = VideoUitls.getDuration(epVideo.getVideoPath());
		if (epVideo.getVideoClip()) {
			long clipTime = (long) ((epVideo.getClipDuration() - epVideo.getClipStart()) * 1000000);
			duration = clipTime < duration ? clipTime : duration;
		}
		//执行命令
		execCmd(cmd, duration, listener);
	}

	/**
	 * 合并多个视频
	 *
	 * @param epVideos     需要合并的视频集合
	 * @param outputOption 输出选项配置
	 */
	public static void merge(List<EpVideo> epVideos, OutputOption outputOption, RxFFmpegInvoke.IFFmpegListener listener) {
		//检测是否有无音轨视频
		boolean isNoAudioTrack = false;
		for (EpVideo epVideo : epVideos) {
			MediaExtractor mediaExtractor = new MediaExtractor();
			try {
				mediaExtractor.setDataSource(epVideo.getVideoPath());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			try {
				int at = TrackUtils.selectAudioTrack(mediaExtractor);
				if (at == -1) {
					isNoAudioTrack = true;
					mediaExtractor.release();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				listener.onError(e.getMessage());
			} finally {
				mediaExtractor.release();
			}

		}
		//设置默认宽高
		outputOption.width = outputOption.width == 0 ? DEFAULT_WIDTH : outputOption.width;
		outputOption.height = outputOption.height == 0 ? DEFAULT_HEIGHT : outputOption.height;
		//判断数量
		if (epVideos.size() > 1) {
			CmdList cmd = new CmdList();
			cmd.append("ffmpeg");
			cmd.append("-y");
			//添加输入标示
			for (EpVideo e : epVideos) {
				if (e.getVideoClip()) {
					cmd.append("-ss").append(e.getClipStart()).append("-t").append(e.getClipDuration()).append("-accurate_seek");
				}
				cmd.append("-i").append(e.getVideoPath());
			}
			for (EpVideo e : epVideos) {
				ArrayList<EpDraw> epDraws = e.getEpDraws();
				if (epDraws.size() > 0) {
					for (EpDraw ep : epDraws) {
						if (ep.isAnimation()) cmd.append("-ignore_loop").append(0);
						cmd.append("-i").append(ep.getPicPath());
					}
				}
			}
			//添加滤镜标识
			cmd.append("-filter_complex");
			StringBuilder filter_complex = new StringBuilder();
			for (int i = 0; i < epVideos.size(); i++) {
				StringBuilder filter = epVideos.get(i).getFilters() == null ? new StringBuilder("") : epVideos.get(i).getFilters().append(",");
				filter_complex.append("[").append(i).append(":v]").append(filter).append("scale=").append(outputOption.width).append(":").append(outputOption.height)
						.append(",setdar=").append(outputOption.getSar()).append("[outv").append(i).append("];");
			}
			//添加标记和处理宽高
			int drawNum = epVideos.size();//图标计数器
			for (int i = 0; i < epVideos.size(); i++) {
				for (int j = 0; j < epVideos.get(i).getEpDraws().size(); j++) {
					filter_complex.append("[").append(drawNum++).append(":0]").append(epVideos.get(i).getEpDraws().get(j).getPicFilter()).append("scale=")
							.append(epVideos.get(i).getEpDraws().get(j).getPicWidth()).append(":").append(epVideos.get(i).getEpDraws().get(j)
							.getPicHeight()).append("[p").append(i).append("a").append(j).append("];");
				}
			}
			//添加图标操作
			for (int i = 0; i < epVideos.size(); i++) {
				for (int j = 0; j < epVideos.get(i).getEpDraws().size(); j++) {
					filter_complex.append("[outv").append(i).append("][p").append(i).append("a").append(j).append("]overlay=")
							.append(epVideos.get(i).getEpDraws().get(j).getPicX()).append(":")
							.append(epVideos.get(i).getEpDraws().get(j).getPicY())
							.append(epVideos.get(i).getEpDraws().get(j).getTime());
					if (epVideos.get(i).getEpDraws().get(j).isAnimation()) {
						filter_complex.append(":shortest=1");
					}
					filter_complex.append("[outv").append(i).append("];");
				}
			}
			//开始合成视频
			for (int i = 0; i < epVideos.size(); i++) {
				filter_complex.append("[outv").append(i).append("]");
			}
			filter_complex.append("concat=n=").append(epVideos.size()).append(":v=1:a=0[outv]");
			//是否添加音轨
			if (!isNoAudioTrack) {
				filter_complex.append(";");
				for (int i = 0; i < epVideos.size(); i++) {
					filter_complex.append("[").append(i).append(":a]");
				}
				filter_complex.append("concat=n=").append(epVideos.size()).append(":v=0:a=1[outa]");
			}
			if (!filter_complex.toString().equals("")) {
				cmd.append(filter_complex.toString());
			}
			cmd.append("-map").append("[outv]");
			if (!isNoAudioTrack) {
				cmd.append("-map").append("[outa]");
			}
			cmd.append(outputOption.getOutputInfo().split(" "));
			cmd.append("-preset").append("superfast").append(outputOption.outPath);
			long duration = 0;
			for (EpVideo ep : epVideos) {
				long d = VideoUitls.getDuration(ep.getVideoPath());
				if (ep.getVideoClip()) {
					long clipTime = (long) ((ep.getClipDuration() - ep.getClipStart()) * 1000000);
					d = clipTime < d ? clipTime : d;
				}
				if (d != 0) {
					duration += d;
				} else {
					break;
				}
			}
			//执行命令
			execCmd(cmd, duration, listener);
		} else {
			throw new RuntimeException("Need more than one video");
		}
	}


	/**
	 * 输出选项设置
	 */
	public static class OutputOption {
		static final int ONE_TO_ONE = 1;// 1:1
		static final int FOUR_TO_THREE = 2;// 4:3
		static final int SIXTEEN_TO_NINE = 3;// 16:9
		static final int NINE_TO_SIXTEEN = 4;// 9:16
		static final int THREE_TO_FOUR = 5;// 3:4

		String outPath;//输出路径
		public int frameRate = 0;//帧率
		public int bitRate = 0;//比特率(一般设置10M)
		public String outFormat = "";//输出格式(目前暂时只支持mp4,x264,mp3,gif)
		private int width = 0;//输出宽度
		private int height = 0;//输出高度
		private int sar = 6;//输出宽高比

		public OutputOption(String outPath) {
			this.outPath = outPath;
		}

		/**
		 * 获取宽高比
		 *
		 * @return 1
		 */
		public String getSar() {
			String res;
			switch (sar) {
				case ONE_TO_ONE:
					res = "1/1";
					break;
				case FOUR_TO_THREE:
					res = "4/3";
					break;
				case THREE_TO_FOUR:
					res = "3/4";
					break;
				case SIXTEEN_TO_NINE:
					res = "16/9";
					break;
				case NINE_TO_SIXTEEN:
					res = "9/16";
					break;
				default:
					res = width + "/" + height;
					break;
			}
			return res;
		}

		public void setSar(int sar) {
			this.sar = sar;
		}

		/**
		 * 获取输出信息
		 *
		 * @return 1
		 */
		String getOutputInfo() {
			StringBuilder res = new StringBuilder();
			if (frameRate != 0) {
				res.append(" -r ").append(frameRate);
			}
			if (bitRate != 0) {
				res.append(" -b ").append(bitRate).append("M");
			}
			if (!outFormat.isEmpty()) {
				res.append(" -f ").append(outFormat);
			}
			return res.toString();
		}

		/**
		 * 设置宽度
		 *
		 * @param width 宽
		 */
		public void setWidth(int width) {
			if (width % 2 != 0) width -= 1;
			this.width = width;
		}

		/**
		 * 设置高度
		 *
		 * @param height 高
		 */
		public void setHeight(int height) {
			if (height % 2 != 0) height -= 1;
			this.height = height;
		}
	}

	/**
	 * 开始处理
	 *
	 * @param cmd              命令
	 * @param duration         视频时长（单位微秒）
	 * @param
	 */
	public static void execCmd(String cmd, long duration, RxFFmpegInvoke.IFFmpegListener listener) {
		cmd = "ffmpeg " + cmd;
		String[] cmds = cmd.split(" ");
		RxFFmpegInvoke.getInstance().runCommandAsync(cmds, listener);
	}

	/**
	 * 开始处理
	 *
	 * @param cmd              命令
	 * @param duration         视频时长（单位微秒）
	 * @param
	 */
	private static void execCmd(CmdList cmd, long duration, RxFFmpegInvoke.IFFmpegListener listener) {
		String[] cmds = cmd.toArray(new String[cmd.size()]);
		String cmdLog = "";
		for (String ss : cmds) {
			cmdLog += ss;
		}
		Log.v("EpMediaF", "cmd:" + cmdLog);
		RxFFmpegInvoke.getInstance().runCommandAsync(cmds, listener);
	}
}

package com.sortinghat.funny.util.business;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.blankj.utilcode.util.ActivityUtils;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.ui.topic.TopicDetailsActivity;
import com.sortinghat.funny.util.RegexUtil;
import com.sortinghat.funny.util.SpannableUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by wzy on 2021/7/21
 */
public class TopicIdentifyUtil {

    public static void identifyPostTitleTopic(int spanTextColor, HomeVideoImageListBean.ListBean.ContentBean videoData, TextView tvPostTitle) {
        String topicNameStr = "";
        if (!TextUtils.isEmpty(videoData.getTopics())) {
            if (videoData.getTopics().startsWith("#")) {
                topicNameStr = videoData.getTopics().substring(1);
            } else {
                topicNameStr = videoData.getTopics();
            }
        }
        String[] topicNames = TextUtils.isEmpty(topicNameStr) ? null : topicNameStr.split("#");
        String[] topicIds = TextUtils.isEmpty(videoData.getTopicIds()) ? null : videoData.getTopicIds().split(",");
        if (topicNames == null || topicIds == null) {
            tvPostTitle.setText(videoData.getTitle());
            return;
        }
        if (topicNames.length != topicIds.length) {
            tvPostTitle.setText(videoData.getTitle());
            return;
        }
        ArrayMap<Integer, Integer> topicNameMap = new ArrayMap<>(5);
        identifyTopic(spanTextColor, videoData.getTitle(), tvPostTitle, null, topicNameMap, -1, topicIds, topicNames);
        addSpaceCharForTopicName(topicNameMap, tvPostTitle);
    }

    private static void addSpaceCharForTopicName(ArrayMap<Integer, Integer> topicNameMap, TextView tvPostTitle) {
        if (topicNameMap != null && !topicNameMap.isEmpty()) {
            Editable edit = tvPostTitle.getEditableText();
            if (edit != null) {
                for (int i = 0; i < topicNameMap.size(); i++) {
                    Integer topicNameStartIndex = topicNameMap.keyAt(i);
                    if (topicNameStartIndex - 2 >= 0) {
                        char topicNamePrevChar = edit.charAt(topicNameStartIndex - 2);
                        if (topicNamePrevChar != ' ') {
                            edit.insert(topicNameStartIndex - 1, " ");
                            topicNameMap = updateTopicNameMap(topicNameMap, " ".length());
                        }
                    }
                    Integer topicNameEndIndex = topicNameMap.valueAt(i);
                    if (topicNameEndIndex <= edit.length() - 1) {
                        char topicNameNextChar = edit.charAt(topicNameEndIndex);
                        if (topicNameNextChar != ' ') {
                            edit.insert(topicNameEndIndex, " ");
                            topicNameMap = updateTopicNameMap(topicNameMap, " ".length());
                        }
                    }
                }
            }
        }
    }

    private static ArrayMap<Integer, Integer> updateTopicNameMap(ArrayMap<Integer, Integer> topicNameMap, int length) {
        ArrayMap<Integer, Integer> newTopicNameMap = new ArrayMap<>(topicNameMap.size());
        for (Map.Entry<Integer, Integer> entry : topicNameMap.entrySet()) {
            newTopicNameMap.put(entry.getKey() + length, entry.getValue() + length);
        }
        return newTopicNameMap;
    }

    public static String identifyTopic(String textContent, TextView textView, List<String> topicNameList, ArrayMap<Integer, Integer> topicNameMap, int editTextCursorPosition) {
        return identifyTopic(0, textContent, textView, topicNameList, topicNameMap, editTextCursorPosition, null, null);
    }

    public static String identifyTopic(int spanTextColor, String textContent, TextView textView, List<String> topicNameList, ArrayMap<Integer, Integer> topicNameMap, int editTextCursorPosition, String[] topicIds, String[] topicNames) {
        textView.setText("");
        for (int i = 0; i < textContent.length(); i++) {
            char c = textContent.charAt(i);
            if (i == textContent.length() - 1) {
                textView.append(String.valueOf(c));
            } else {
                if (c != '#') {
                    textView.append(String.valueOf(c));
                    continue;
                }
            }
            for (int j = i + 1; j < textContent.length(); j++) {
                String ch = String.valueOf(textContent.charAt(j));
                if (j == textContent.length() - 1) {
                    if (j == i + 1) {
                        if (RegexUtil.isHasSpecailString(ch)) {
                            textView.append(String.valueOf('#'));
                            textView.append(ch);
                            i = j;
                        } else {
                            textView.append(SpannableUtil.getTextSpan(textContent.substring(i, j + 1), textView, topicNameList == null ? new MyClickableSpan(spanTextColor, textContent.substring(i + 1, j + 1), getTopicId(textContent.substring(i + 1, j + 1), topicIds, topicNames)) : null));
                            if (topicNameList != null) {
                                topicNameList.add(textContent.substring(i + 1, j + 1));
                            }
                            if (topicNameMap != null) {
                                topicNameMap.put(i + 1, j + 1);
                            }
                            i = j;
                        }
                    } else {
                        if (RegexUtil.isHasSpecailString(ch)) {
                            textView.append(SpannableUtil.getTextSpan(textContent.substring(i, j), textView, topicNameList == null ? new MyClickableSpan(spanTextColor, textContent.substring(i + 1, j), getTopicId(textContent.substring(i + 1, j), topicIds, topicNames)) : null));
                            textView.append(ch);
                            if (topicNameList != null) {
                                topicNameList.add(textContent.substring(i + 1, j));
                            }
                            if (topicNameMap != null) {
                                topicNameMap.put(i + 1, j);
                            }
                            i = j;
                        } else {
                            textView.append(SpannableUtil.getTextSpan(textContent.substring(i, j + 1), textView, topicNameList == null ? new MyClickableSpan(spanTextColor, textContent.substring(i + 1, j + 1), getTopicId(textContent.substring(i + 1, j + 1), topicIds, topicNames)) : null));
                            if (topicNameList != null) {
                                topicNameList.add(textContent.substring(i + 1, j + 1));
                            }
                            if (topicNameMap != null) {
                                topicNameMap.put(i + 1, j + 1);
                            }
                            i = j;
                        }
                    }
                } else {
                    if (j == i + 1) {
                        if (RegexUtil.isHasSpecailString(ch)) {
                            textView.append(String.valueOf('#'));
                            if (!"#".equals(ch)) {
                                textView.append(ch);
                                i = j;
                            } else {
                                i = j - 1;
                            }
                            break;
                        }
                    } else {
                        if (RegexUtil.isHasSpecailString(ch)) {
                            textView.append(SpannableUtil.getTextSpan(textContent.substring(i, j), textView, topicNameList == null ? new MyClickableSpan(spanTextColor, textContent.substring(i + 1, j), getTopicId(textContent.substring(i + 1, j), topicIds, topicNames)) : null));
                            if (!"#".equals(ch)) {
                                textView.append(ch);
                            }
                            if (topicNameList != null) {
                                topicNameList.add(textContent.substring(i + 1, j));
                            }
                            if (topicNameMap != null) {
                                topicNameMap.put(i + 1, j);
                            }
                            if (!"#".equals(ch)) {
                                i = j;
                            } else {
                                i = j - 1;
                            }
                            break;
                        }
                    }
                }
            }
        }

        String searchTopicName = "";
        if (topicNameMap != null && !topicNameMap.isEmpty()) {
            for (Map.Entry<Integer, Integer> entry : topicNameMap.entrySet()) {
                if (editTextCursorPosition >= entry.getKey() && editTextCursorPosition <= entry.getValue()) {
                    searchTopicName = textContent.substring(entry.getKey(), entry.getValue());
                }
            }
        }

        return searchTopicName;
    }

    private static String getTopicId(String mTopicName, String[] topicIds, String[] topicNames) {
        String topicId = "";
        if (topicNames != null) {
            for (int i = 0; i < topicNames.length; i++) {
                String topicName = topicNames[i];
                if (mTopicName.equals(topicName)) {
                    topicId = topicIds == null ? "" : topicIds[i];
                    break;
                }
            }
        }
        return topicId;
    }

    private static class MyClickableSpan extends ClickableSpan {

        private int spanTextColor;
        private String topicName;
        private String topicId;

        public MyClickableSpan(int spanTextColor, String topicName, String topicId) {
            this.spanTextColor = spanTextColor;
            this.topicName = topicName;
            this.topicId = topicId;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(CommonUtils.getColor(spanTextColor == 0 ? R.color.light_orange : spanTextColor));
            ds.setFakeBoldText(true);
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(@NonNull View widget) {
            Bundle bundle = new Bundle();
            bundle.putString("TOPIC_ID", topicId);
            bundle.putString("TOPIC_NAME", topicName);
            ActivityUtils.startActivity(bundle, TopicDetailsActivity.class);
        }
    }
}

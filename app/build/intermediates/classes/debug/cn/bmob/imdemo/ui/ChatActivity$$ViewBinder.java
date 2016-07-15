// Generated code from Butter Knife. Do not modify!
package cn.bmob.imdemo.ui;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ChatActivity$$ViewBinder<T extends cn.bmob.imdemo.ui.ChatActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492944, "field 'll_chat'");
    target.ll_chat = finder.castView(view, 2131492944, "field 'll_chat'");
    view = finder.findRequiredView(source, 2131492945, "field 'sw_refresh'");
    target.sw_refresh = finder.castView(view, 2131492945, "field 'sw_refresh'");
    view = finder.findRequiredView(source, 2131492946, "field 'rc_view'");
    target.rc_view = finder.castView(view, 2131492946, "field 'rc_view'");
    view = finder.findRequiredView(source, 2131493011, "field 'edit_msg' and method 'onEditClick'");
    target.edit_msg = finder.castView(view, 2131493011, "field 'edit_msg'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onEditClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493009, "field 'btn_chat_add' and method 'onAddClick'");
    target.btn_chat_add = finder.castView(view, 2131493009, "field 'btn_chat_add'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onAddClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493010, "field 'btn_chat_emo' and method 'onEmoClick'");
    target.btn_chat_emo = finder.castView(view, 2131493010, "field 'btn_chat_emo'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onEmoClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493012, "field 'btn_speak'");
    target.btn_speak = finder.castView(view, 2131493012, "field 'btn_speak'");
    view = finder.findRequiredView(source, 2131493013, "field 'btn_chat_voice' and method 'onVoiceClick'");
    target.btn_chat_voice = finder.castView(view, 2131493013, "field 'btn_chat_voice'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onVoiceClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493014, "field 'btn_chat_keyboard' and method 'onKeyClick'");
    target.btn_chat_keyboard = finder.castView(view, 2131493014, "field 'btn_chat_keyboard'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onKeyClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493015, "field 'btn_chat_send' and method 'onSendClick'");
    target.btn_chat_send = finder.castView(view, 2131493015, "field 'btn_chat_send'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onSendClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493016, "field 'layout_more'");
    target.layout_more = finder.castView(view, 2131493016, "field 'layout_more'");
    view = finder.findRequiredView(source, 2131493019, "field 'layout_add'");
    target.layout_add = finder.castView(view, 2131493019, "field 'layout_add'");
    view = finder.findRequiredView(source, 2131493017, "field 'layout_emo'");
    target.layout_emo = finder.castView(view, 2131493017, "field 'layout_emo'");
    view = finder.findRequiredView(source, 2131492947, "field 'layout_record'");
    target.layout_record = finder.castView(view, 2131492947, "field 'layout_record'");
    view = finder.findRequiredView(source, 2131492949, "field 'tv_voice_tips'");
    target.tv_voice_tips = finder.castView(view, 2131492949, "field 'tv_voice_tips'");
    view = finder.findRequiredView(source, 2131492948, "field 'iv_record'");
    target.iv_record = finder.castView(view, 2131492948, "field 'iv_record'");
    view = finder.findRequiredView(source, 2131493006, "method 'onPictureClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onPictureClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493007, "method 'onCameraClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onCameraClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493008, "method 'onLocationClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onLocationClick(p0);
        }
      });
  }

  @Override public void unbind(T target) {
    target.ll_chat = null;
    target.sw_refresh = null;
    target.rc_view = null;
    target.edit_msg = null;
    target.btn_chat_add = null;
    target.btn_chat_emo = null;
    target.btn_speak = null;
    target.btn_chat_voice = null;
    target.btn_chat_keyboard = null;
    target.btn_chat_send = null;
    target.layout_more = null;
    target.layout_add = null;
    target.layout_emo = null;
    target.layout_record = null;
    target.tv_voice_tips = null;
    target.iv_record = null;
  }
}

// Generated code from Butter Knife. Do not modify!
package cn.bmob.imdemo.adapter;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SendVoiceHolder$$ViewBinder<T extends cn.bmob.imdemo.adapter.SendVoiceHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493028, "field 'iv_avatar'");
    target.iv_avatar = finder.castView(view, 2131493028, "field 'iv_avatar'");
    view = finder.findRequiredView(source, 2131493035, "field 'iv_fail_resend'");
    target.iv_fail_resend = finder.castView(view, 2131493035, "field 'iv_fail_resend'");
    view = finder.findRequiredView(source, 2131493026, "field 'tv_time'");
    target.tv_time = finder.castView(view, 2131493026, "field 'tv_time'");
    view = finder.findRequiredView(source, 2131493034, "field 'tv_voice_length'");
    target.tv_voice_length = finder.castView(view, 2131493034, "field 'tv_voice_length'");
    view = finder.findRequiredView(source, 2131493033, "field 'iv_voice'");
    target.iv_voice = finder.castView(view, 2131493033, "field 'iv_voice'");
    view = finder.findRequiredView(source, 2131493036, "field 'tv_send_status'");
    target.tv_send_status = finder.castView(view, 2131493036, "field 'tv_send_status'");
    view = finder.findRequiredView(source, 2131493030, "field 'progress_load'");
    target.progress_load = finder.castView(view, 2131493030, "field 'progress_load'");
  }

  @Override public void unbind(T target) {
    target.iv_avatar = null;
    target.iv_fail_resend = null;
    target.tv_time = null;
    target.tv_voice_length = null;
    target.iv_voice = null;
    target.tv_send_status = null;
    target.progress_load = null;
  }
}

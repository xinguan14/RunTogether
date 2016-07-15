// Generated code from Butter Knife. Do not modify!
package cn.bmob.imdemo.adapter;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ReceiveTextHolder$$ViewBinder<T extends cn.bmob.imdemo.adapter.ReceiveTextHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493028, "field 'iv_avatar' and method 'onAvatarClick'");
    target.iv_avatar = finder.castView(view, 2131493028, "field 'iv_avatar'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onAvatarClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493026, "field 'tv_time'");
    target.tv_time = finder.castView(view, 2131493026, "field 'tv_time'");
    view = finder.findRequiredView(source, 2131493027, "field 'tv_message'");
    target.tv_message = finder.castView(view, 2131493027, "field 'tv_message'");
  }

  @Override public void unbind(T target) {
    target.iv_avatar = null;
    target.tv_time = null;
    target.tv_message = null;
  }
}

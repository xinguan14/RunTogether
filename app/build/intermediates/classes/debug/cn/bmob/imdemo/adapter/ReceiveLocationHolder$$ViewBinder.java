// Generated code from Butter Knife. Do not modify!
package cn.bmob.imdemo.adapter;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ReceiveLocationHolder$$ViewBinder<T extends cn.bmob.imdemo.adapter.ReceiveLocationHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493028, "field 'iv_avatar'");
    target.iv_avatar = finder.castView(view, 2131493028, "field 'iv_avatar'");
    view = finder.findRequiredView(source, 2131493026, "field 'tv_time'");
    target.tv_time = finder.castView(view, 2131493026, "field 'tv_time'");
    view = finder.findRequiredView(source, 2131493008, "field 'tv_location'");
    target.tv_location = finder.castView(view, 2131493008, "field 'tv_location'");
  }

  @Override public void unbind(T target) {
    target.iv_avatar = null;
    target.tv_time = null;
    target.tv_location = null;
  }
}

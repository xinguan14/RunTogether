// Generated code from Butter Knife. Do not modify!
package cn.bmob.imdemo.ui.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SetFragment$$ViewBinder<T extends cn.bmob.imdemo.ui.fragment.SetFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492991, "field 'tv_set_name'");
    target.tv_set_name = finder.castView(view, 2131492991, "field 'tv_set_name'");
    view = finder.findRequiredView(source, 2131492990, "field 'layout_info' and method 'onInfoClick'");
    target.layout_info = finder.castView(view, 2131492990, "field 'layout_info'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onInfoClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493004, "method 'onLogoutClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onLogoutClick(p0);
        }
      });
  }

  @Override public void unbind(T target) {
    target.tv_set_name = null;
    target.layout_info = null;
  }
}

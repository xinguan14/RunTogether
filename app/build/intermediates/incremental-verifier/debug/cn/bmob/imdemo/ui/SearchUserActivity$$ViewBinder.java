// Generated code from Butter Knife. Do not modify!
package cn.bmob.imdemo.ui;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SearchUserActivity$$ViewBinder<T extends cn.bmob.imdemo.ui.SearchUserActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492969, "field 'et_find_name'");
    target.et_find_name = finder.castView(view, 2131492969, "field 'et_find_name'");
    view = finder.findRequiredView(source, 2131492945, "field 'sw_refresh'");
    target.sw_refresh = finder.castView(view, 2131492945, "field 'sw_refresh'");
    view = finder.findRequiredView(source, 2131492970, "field 'btn_search' and method 'onSearchClick'");
    target.btn_search = finder.castView(view, 2131492970, "field 'btn_search'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onSearchClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492946, "field 'rc_view'");
    target.rc_view = finder.castView(view, 2131492946, "field 'rc_view'");
  }

  @Override public void unbind(T target) {
    target.et_find_name = null;
    target.sw_refresh = null;
    target.btn_search = null;
    target.rc_view = null;
  }
}

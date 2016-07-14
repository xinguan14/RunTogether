// Generated code from Butter Knife. Do not modify!
package cn.bmob.imdemo.adapter;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SearchUserHolder$$ViewBinder<T extends cn.bmob.imdemo.adapter.SearchUserHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493043, "field 'avatar'");
    target.avatar = finder.castView(view, 2131493043, "field 'avatar'");
    view = finder.findRequiredView(source, 2131493044, "field 'name'");
    target.name = finder.castView(view, 2131493044, "field 'name'");
    view = finder.findRequiredView(source, 2131493045, "field 'btn_add'");
    target.btn_add = finder.castView(view, 2131493045, "field 'btn_add'");
  }

  @Override public void unbind(T target) {
    target.avatar = null;
    target.name = null;
    target.btn_add = null;
  }
}

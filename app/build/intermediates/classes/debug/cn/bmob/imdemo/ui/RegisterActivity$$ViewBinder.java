// Generated code from Butter Knife. Do not modify!
package cn.bmob.imdemo.ui;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class RegisterActivity$$ViewBinder<T extends cn.bmob.imdemo.ui.RegisterActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492951, "field 'et_username'");
    target.et_username = finder.castView(view, 2131492951, "field 'et_username'");
    view = finder.findRequiredView(source, 2131492952, "field 'et_password'");
    target.et_password = finder.castView(view, 2131492952, "field 'et_password'");
    view = finder.findRequiredView(source, 2131492967, "field 'btn_register' and method 'onRegisterClick'");
    target.btn_register = finder.castView(view, 2131492967, "field 'btn_register'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onRegisterClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492966, "field 'et_password_again'");
    target.et_password_again = finder.castView(view, 2131492966, "field 'et_password_again'");
  }

  @Override public void unbind(T target) {
    target.et_username = null;
    target.et_password = null;
    target.btn_register = null;
    target.et_password_again = null;
  }
}

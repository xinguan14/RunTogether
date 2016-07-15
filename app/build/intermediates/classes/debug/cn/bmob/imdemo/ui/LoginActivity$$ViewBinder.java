// Generated code from Butter Knife. Do not modify!
package cn.bmob.imdemo.ui;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class LoginActivity$$ViewBinder<T extends cn.bmob.imdemo.ui.LoginActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492951, "field 'et_username'");
    target.et_username = finder.castView(view, 2131492951, "field 'et_username'");
    view = finder.findRequiredView(source, 2131492952, "field 'et_password'");
    target.et_password = finder.castView(view, 2131492952, "field 'et_password'");
    view = finder.findRequiredView(source, 2131492953, "field 'btn_login' and method 'onLoginClick'");
    target.btn_login = finder.castView(view, 2131492953, "field 'btn_login'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onLoginClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492954, "field 'tv_register' and method 'onRegisterClick'");
    target.tv_register = finder.castView(view, 2131492954, "field 'tv_register'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onRegisterClick(p0);
        }
      });
  }

  @Override public void unbind(T target) {
    target.et_username = null;
    target.et_password = null;
    target.btn_login = null;
    target.tv_register = null;
  }
}

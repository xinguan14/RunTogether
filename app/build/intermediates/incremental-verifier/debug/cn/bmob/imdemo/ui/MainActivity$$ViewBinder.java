// Generated code from Butter Knife. Do not modify!
package cn.bmob.imdemo.ui;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends cn.bmob.imdemo.ui.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492956, "field 'btn_sports'");
    target.btn_sports = finder.castView(view, 2131492956, "field 'btn_sports'");
    view = finder.findRequiredView(source, 2131492958, "field 'btn_find'");
    target.btn_find = finder.castView(view, 2131492958, "field 'btn_find'");
    view = finder.findRequiredView(source, 2131492960, "field 'btn_run'");
    target.btn_run = finder.castView(view, 2131492960, "field 'btn_run'");
    view = finder.findRequiredView(source, 2131492962, "field 'btn_connect'");
    target.btn_connect = finder.castView(view, 2131492962, "field 'btn_connect'");
    view = finder.findRequiredView(source, 2131492964, "field 'btn_set'");
    target.btn_set = finder.castView(view, 2131492964, "field 'btn_set'");
    view = finder.findRequiredView(source, 2131492957, "field 'iv_sports_tips'");
    target.iv_sports_tips = finder.castView(view, 2131492957, "field 'iv_sports_tips'");
    view = finder.findRequiredView(source, 2131492959, "field 'iv_find_tips'");
    target.iv_find_tips = finder.castView(view, 2131492959, "field 'iv_find_tips'");
    view = finder.findRequiredView(source, 2131492961, "field 'iv_run_tips'");
    target.iv_run_tips = finder.castView(view, 2131492961, "field 'iv_run_tips'");
    view = finder.findRequiredView(source, 2131492963, "field 'iv_connect_tips'");
    target.iv_connect_tips = finder.castView(view, 2131492963, "field 'iv_connect_tips'");
  }

  @Override public void unbind(T target) {
    target.btn_sports = null;
    target.btn_find = null;
    target.btn_run = null;
    target.btn_connect = null;
    target.btn_set = null;
    target.iv_sports_tips = null;
    target.iv_find_tips = null;
    target.iv_run_tips = null;
    target.iv_connect_tips = null;
  }
}

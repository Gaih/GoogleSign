package com.yilegame.yile.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yilegame.http.Impl.HttpCallBack;
import com.yilegame.http.Mlog;
import com.yilegame.http.constant.QYConstant;
import com.yilegame.http.uti.YiLeHttpUtils;
import com.yilegame.yile.wight.AccountEditText;

import java.util.ArrayList;


public class SetPwdQuestionsActivity extends BaseActivity {
	private TextView tv_cancle;
	private Spinner spinner_question2;
	private Spinner spinner_question1;
	private AccountEditText aet_answer2;
	private AccountEditText aet_answer1;
	public static SetPwdQuestionsActivity instance;
	private CheckBox cb;
	private ArrayList<String> questions;
	private EditText et_question1;
	private EditText et_question2;
	private String question1;
	private String question2;
	private String answer1 ;
	private String answer2 ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(Utils.getLayoutId(getApplicationContext(), "activity_set_pwdquestion"));
		instance=this;
		cb = (CheckBox)findViewById(Utils.getId(getApplicationContext(), "cb"));
		questions=new ArrayList<String>();
		questions.add("你中学的全名是");
		questions.add("你看的第一部电影是");
		questions.add("你的出生地址是哪里");
		questions.add("你年少时最好的朋友是谁");
		questions.add("你最喜欢的颜色是");
		questions.add("你小学时候的班主任是");
		questions.add("你另一半的名字是");
		questions.add("你最喜欢的影视明星是");
		questions.add("自定义");
		spinner_question2 = (Spinner)findViewById(Utils.getId(getApplicationContext(), "spinner_question2"));
		spinner_question1 = (Spinner)findViewById(Utils.getId(getApplicationContext(), "spinner_question1"));
		et_question1 = (EditText)findViewById(Utils.getId(getApplicationContext(), "et_question1"));
		et_question2 = (EditText)findViewById(Utils.getId(getApplicationContext(), "et_question2"));
		aet_answer2 = (AccountEditText)findViewById(Utils.getId(getApplicationContext(), "Aet_answer2"));
		aet_answer1 = (AccountEditText)findViewById(Utils.getId(getApplicationContext(), "Aet_answer1"));
		TextView tv_sumbit=(TextView)findViewById(Utils.getId(getApplicationContext(), "tv_sumbit"));
		spinner_question1.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int postion, long arg3) {
				TextView v=(TextView) view;
				Mlog.d("feng", "1select :"+postion+"/"+(questions.size()-1)+"/"+"arg3:"+arg3);
				if (postion==questions.size()-1) {
					Mlog.d("feng", "1select zidianyi");
					v.setText("");
					et_question1.setVisibility(View.VISIBLE);
				}else{
					et_question1.setVisibility(View.GONE);
					Mlog.d("feng", "1select zidianyi2");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		spinner_question2.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int postion, long arg3) {
				TextView v=(TextView) view;
					if (postion==questions.size()-1) {
						Mlog.d("feng", "2select zidianyi");
						v.setText("");
						et_question2.setVisibility(View.VISIBLE);
					}else{
						Mlog.d("feng", "2select zidianyi1");
						et_question2.setVisibility(View.GONE);
					}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		ArrayAdapter<String> questionAdapter = new ArrayAdapter<String>(getApplicationContext(),
				Utils.getLayoutId(getApplicationContext(), "spinner_item"),questions);
		spinner_question1.setAdapter(questionAdapter);
		spinner_question2.setAdapter(questionAdapter);
		spinner_question2.setSelection(4);
		title=(TextView) findViewById(Utils.getId(getApplicationContext(), "title"));
		title.setText("您的账号存在风险,建议设置密保问题。");
		title.setTextColor(getResources().getColor(Utils.getColorId(getApplicationContext(), "orange")));
		LinearLayout ll_return = (LinearLayout) findViewById(Utils.getId(getApplicationContext(), "ll_return"));
		ll_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				finish();
			
			}
		});
		tv_sumbit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (spinner_question1.getSelectedItemPosition()==questions.size()-1) {//自定义
					question1=et_question1.getText().toString().trim();
				}else{
					question1 = questions.get(spinner_question1.getSelectedItemPosition());
				}
				if (spinner_question2.getSelectedItemPosition()==questions.size()-1) {//自定义
					question2=et_question2.getText().toString().trim();
				}else{
					question2 =questions.get(spinner_question2.getSelectedItemPosition());
				}
				  answer1 = aet_answer1.getText();
				  answer2 = aet_answer2.getText();
				if (cb.isChecked()) {
					QYConstant.setNoPwdQuestion(SetPwdQuestionsActivity.this, cb.isChecked());
				}
				if (TextUtils.isEmpty(question1)||TextUtils.isEmpty(question2)) {
					Toast.makeText(SetPwdQuestionsActivity.this, "问题不能为空", Toast.LENGTH_LONG).show();
					return;
				}
				if (TextUtils.isEmpty(answer1)||TextUtils.isEmpty(answer2)) {
					Toast.makeText(SetPwdQuestionsActivity.this, "答案不能为空", Toast.LENGTH_LONG).show();
					return;
				}
				if (question1.equals(question2)) {
					Toast.makeText(SetPwdQuestionsActivity.this, "两次问题不能相同", Toast.LENGTH_LONG).show();
					return;
				}
				if (TextUtils.isEmpty(aet_answer1.getText())||TextUtils.isEmpty(aet_answer2.getText())) {
					Toast.makeText(SetPwdQuestionsActivity.this, "密保问题不能为空", Toast.LENGTH_LONG).show();
					return;
				}
				showLoading();
				YiLeHttpUtils.setQuestion(SetPwdQuestionsActivity.this, question1, answer1,new HttpCallBack() {
					
					@Override
					public void getData(Object o) {
						boolean b=(Boolean) o;
						if (b) {//第一次发送成功
							YiLeHttpUtils.setQuestion(SetPwdQuestionsActivity.this, question2, answer2,new HttpCallBack() {
								
								@Override
								public void getData(Object o) {
									boolean b=(Boolean) o;
									if (b){
										finish();
										showErrorView("设置成功");
										hideLoading();
									}else{
										showErrorView("设置失败");
										hideLoading();
									}
								}
							});
						}else{
							showErrorView("设置失败");
							hideLoading();
						}
					}
				});
//				YiLeHttpUtils.setSafeRequest(SetPwdQuestionsActivity.this, question1, answer1, question2, answer2);
			}
		});
		tv_cancle = (TextView) findViewById(Utils.getId(getApplicationContext(), "tv_cancle"));
		tv_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cb.isChecked()) {
					QYConstant.setNoPwdQuestion(SetPwdQuestionsActivity.this, cb.isChecked());
				}
				finish();
			}
		});
	}

		
	
	
}

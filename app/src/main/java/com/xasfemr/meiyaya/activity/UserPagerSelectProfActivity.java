package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.main.BaseActivity;

public class UserPagerSelectProfActivity extends BaseActivity {

    private final String[]  professionArr   = {"美容机构代表/美容院院长", "美容院店长", "高级美容师", "美业技师/学徒", "整形医生/高级护理", "高级美容顾问/咨询师", "美业协会工作人员", "美业产品、仪器生产厂商/代理商、供应商", "美业产品销售人员", "美容治疗师", "美导（助教老师）", "美容行业教育培训人员", "美业仪器操作师", "美业其他从业人员", "其他行业"};
    private       boolean[] profSelectedArr = new boolean[professionArr.length];

    private TextView     tvTopRight;
    private RecyclerView rvProfession;
    private RadioGroup   rgProfession;
    private Intent       mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pager_select_prof);
        initTopBar();
        setTopTitleText("选择职位");

        tvTopRight = (TextView) findViewById(R.id.tv_top_right);
        rvProfession = (RecyclerView) findViewById(R.id.rv_profession);
        rgProfession = (RadioGroup) findViewById(R.id.rg_profession);

        mIntent = getIntent();
        int profession = mIntent.getIntExtra("PROFESSION", 0);
        //rgProfession.check(R.id.rb_profession_1);

        rgProfession.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                switch (checkedId) {
                    case R.id.rb_profession_1:
                        mIntent.putExtra("ProfSelected", 0);
                        break;
                    case R.id.rb_profession_2:
                        mIntent.putExtra("ProfSelected", 1);
                        break;
                    case R.id.rb_profession_3:
                        mIntent.putExtra("ProfSelected", 2);
                        break;
                    case R.id.rb_profession_4:
                        mIntent.putExtra("ProfSelected", 3);
                        break;
                    case R.id.rb_profession_5:
                        mIntent.putExtra("ProfSelected", 4);
                        break;
                    case R.id.rb_profession_6:
                        mIntent.putExtra("ProfSelected", 5);
                        break;
                    case R.id.rb_profession_7:
                        mIntent.putExtra("ProfSelected", 6);
                        break;
                    case R.id.rb_profession_8:
                        mIntent.putExtra("ProfSelected", 7);
                        break;
                    case R.id.rb_profession_9:
                        mIntent.putExtra("ProfSelected", 8);
                        break;
                    case R.id.rb_profession_10:
                        mIntent.putExtra("ProfSelected", 9);
                        break;
                    case R.id.rb_profession_11:
                        mIntent.putExtra("ProfSelected", 10);
                        break;
                    case R.id.rb_profession_12:
                        mIntent.putExtra("ProfSelected", 11);
                        break;
                    case R.id.rb_profession_13:
                        mIntent.putExtra("ProfSelected", 12);
                        break;
                    case R.id.rb_profession_14:
                        mIntent.putExtra("ProfSelected", 13);
                        break;
                    case R.id.rb_profession_15:
                        mIntent.putExtra("ProfSelected", 14);
                        break;
                    default:
                        break;
                }
                setResult(27, mIntent);
                finish();
            }
        });

        rvProfession.setLayoutManager(new LinearLayoutManager(this));
        rvProfession.setAdapter(new ProfessionAdapter());

        //右上角显示'保存'
        //initView();
    }

    private void initView() {
        tvTopRight.setVisibility(View.VISIBLE);
        tvTopRight.setText("保存");
        tvTopRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }


    private class ProfessionAdapter extends RecyclerView.Adapter<ProfessionHolder> {

        @Override
        public ProfessionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(UserPagerSelectProfActivity.this, R.layout.item_profession, null);
            ProfessionHolder professionHolder = new ProfessionHolder(view);
            return professionHolder;
        }

        @Override
        public void onBindViewHolder(final ProfessionHolder holder, final int position) {
            holder.tvProfName.setText(professionArr[position]);

            if (profSelectedArr[position]) {
                holder.ivProfSelected.setVisibility(View.VISIBLE);
            } else {
                holder.ivProfSelected.setVisibility(View.INVISIBLE);
            }

            //点击事件处理
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (profSelectedArr[position]) {
                        holder.ivProfSelected.setVisibility(View.INVISIBLE);
                        profSelectedArr[position] = false;
                    } else {

                        for (int i = 0; i < profSelectedArr.length; i++) {
                            if (profSelectedArr[i]) {
                                Toast.makeText(UserPagerSelectProfActivity.this, "您已选择职业,请先取消", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        holder.ivProfSelected.setVisibility(View.VISIBLE);
                        profSelectedArr[position] = true;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return professionArr.length;
        }
    }

    static class ProfessionHolder extends RecyclerView.ViewHolder {
        public TextView  tvProfName;
        public ImageView ivProfSelected;

        public ProfessionHolder(View itemView) {
            super(itemView);
            tvProfName = (TextView) itemView.findViewById(R.id.tv_prof_name);
            ivProfSelected = (ImageView) itemView.findViewById(R.id.iv_prof_selected);
        }
    }
}

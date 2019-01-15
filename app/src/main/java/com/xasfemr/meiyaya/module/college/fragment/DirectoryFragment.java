 package com.xasfemr.meiyaya.module.college.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.module.college.protocol.ExcellentDiretoryProtocol;
import com.xasfemr.meiyaya.utils.LogUtils;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;


 /**
 * 精品课程---目录
 * Created by sen.luo on 2017/12/18.
 */

public class DirectoryFragment extends Fragment{

     private ArrayList<ExcellentDiretoryProtocol> excellentLsit;

//     @BindView(R.id.lvDirectory)
     ListView lvDirectory;

     private ExcellentDiretoryAdapter adapter;

     private int selectionId =0;

     public DirectoryFragment(ArrayList<ExcellentDiretoryProtocol> excellentCourseLsit) {
         this.excellentLsit=excellentCourseLsit;
     }

     @Nullable
     @Override
     public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_directory,null);
         lvDirectory= (ListView) view.findViewById(R.id.lvDirectory);

         adapter=new ExcellentDiretoryAdapter(getActivity(),excellentLsit);
         lvDirectory.setAdapter(adapter);

         LogUtils.show("listchang度",excellentLsit.size()+"");

         lvDirectory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                 if (excellentLsit.size()==1){
                     return;
                 }else {
                     selectionId=position;
                     adapter.notifyDataSetChanged();
                     EventBus.getDefault().post(excellentLsit.get(position).origurl, GlobalConstants.EventBus.CHANGE_PLAYER_PATH);
                 }


             }
         });

         return view;
     }
     public class ExcellentDiretoryAdapter extends BaseAdapter {

         private ArrayList<ExcellentDiretoryProtocol> excellentLsit;
         private Context context;

         public ExcellentDiretoryAdapter(Context context,ArrayList<ExcellentDiretoryProtocol> excellentList) {
             this.excellentLsit = excellentList;
             this.context = context;

         }

         @Override
         public int getCount() {
             return excellentLsit.size();
         }

         @Override
         public Object getItem(int position) {
             return excellentLsit.get(position);
         }

         @Override
         public long getItemId(int position) {
             return position;
         }

         @Override
         public View getView(int position, View convertView, ViewGroup parent) {

             DiretoryViewHolder diretoryViewHolder =null;

             if (convertView==null){
                 diretoryViewHolder =new DiretoryViewHolder();
                 convertView= LayoutInflater.from(context).inflate(R.layout.item_excellent_diretory,null);
                 diretoryViewHolder.tvName= (TextView) convertView.findViewById(R.id.tvItemName);
                 diretoryViewHolder.tvStatus= (TextView) convertView.findViewById(R.id.tvItemdes);
                 diretoryViewHolder.layout_item= (LinearLayout) convertView.findViewById(R.id.layout_item);

                 convertView.setTag(diretoryViewHolder);

             }else {
                 diretoryViewHolder= (DiretoryViewHolder) convertView.getTag();
             }

             diretoryViewHolder.tvName.setText(position+1+"、"+excellentLsit.get(position).title);
             diretoryViewHolder.tvStatus.setText(excellentLsit.get(position).fee);


             if (selectionId==position){
                 diretoryViewHolder.tvName.setSelected(true);
             }else {
                 diretoryViewHolder.tvName.setSelected(false);
             }

             return convertView;
         }


        public class DiretoryViewHolder{
             TextView tvName,tvStatus;
             LinearLayout layout_item;
         }


     }



}


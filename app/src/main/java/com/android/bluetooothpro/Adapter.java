package com.android.bluetooothpro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.PostsViewHolder> {

    Context context;
    ArrayList<GetSet> arrayList;
    private List<GetSet> list;
    private AdapterView.OnItemClickListener listener;

    public Adapter() {
    }

    public Adapter(List<GetSet> hList, AdapterView.OnItemClickListener listener) {
        this.list = hList;
        this.listener = listener;
    }

    public Adapter(Context c, ArrayList<GetSet> h) {
        context = c;
        arrayList = h;
    }

    @NonNull
    @Override
    public Adapter.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostsViewHolder(LayoutInflater.from(context).inflate(R.layout.device_recycler,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Adapter.PostsViewHolder holder, int position) {

        holder.bind(arrayList.get(position), listener);

        holder.bname.setText(arrayList.get(position).getBname());
        holder.bdatetime.setText(arrayList.get(position).getBdatetime());
        holder.bdist.setText(arrayList.get(position).getBdist()+"m");

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(GetSet GetSet);
    }

    public class PostsViewHolder extends RecyclerView.ViewHolder {

        TextView bname, bdatetime, bdist;

        public PostsViewHolder(View itemView) {

            super(itemView);

            bname = itemView.findViewById(R.id.bnametxt);
            bdist = itemView.findViewById(R.id.bdisttxt);
            bdatetime = itemView.findViewById(R.id.bdatetimetxt);

        }

        public void bind(final GetSet item, final AdapterView.OnItemClickListener listener) {

        }
    }
}

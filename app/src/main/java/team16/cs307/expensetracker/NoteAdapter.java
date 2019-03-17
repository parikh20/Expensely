package team16.cs307.expensetracker;



import android.support.annotation.NonNull;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;





public class NoteAdapter extends FirestoreRecyclerAdapter<Note,NoteAdapter.ViewHolder> {
    private OnItemClickListener listener;
    private OnItemLongClickListener longlistener;
    String downloadUrl;
    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
    }

    /*private ArrayList<String> urls;
    private Context context;


    public NoteAdapter(Context context) {
        urls = new ArrayList<String>();
        this.context = context;
    }

    @Override
    public void onBindViewHolder(NoteAdapter.ViewHolder holder, int positionl) {

        //holder.textDescription.setText();

    }*/

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Note model) {
        holder.dateDescription.setText(model.getDate());
        holder.tag.setText(model.getTag());
        downloadUrl = "https://firebasestorage.googleapis.com/v0/b/expensely-cs307.appspot.com/o/"+model.getImgurl();
        Picasso.get().load(downloadUrl).fit().centerCrop().into(holder.img);


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_item,viewGroup,false);
        return new ViewHolder(v);
    }

    public void deleteItem( int position){
        getSnapshots().getSnapshot(position).getReference().delete();

    }
    /*@Override
    public int getItemCount() {
        return urls.size();
    }



    public void setUrls (@NonNull ArrayList<String> urlList) {
        /*if (urls != null) {
            urls.clear();
        }*/
        /*urls.addAll(urlList);
        this.notifyItemRangeInserted(0,urlList.size() - 1);
        System.out.println(urlList);
    }*/

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView dateDescription;
        private TextView tag;

        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.image_item_ImageView);
            dateDescription= view.findViewById(R.id.image_item_Description);
            tag = view.findViewById(R.id.image_item_Tag);

            img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        longlistener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                    return false;
                }
            });
            tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });

        }


    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot,int position);
    }

    public interface OnItemLongClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot,int position);
    }




    public void setLongClickListener(OnItemLongClickListener longlistener){
        this.longlistener=longlistener;
    }
    public void TagClickLisentner(OnItemClickListener listener){this.listener=listener;}



}

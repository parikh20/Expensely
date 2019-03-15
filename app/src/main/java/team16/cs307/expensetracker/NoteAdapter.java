package team16.cs307.expensetracker;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note,NoteAdapter.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
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
        downloadUrl = "https://firebasestorage.googleapis.com/v0/b/expensely-cs307.appspot.com/o/"+model.getImgurl();
        Picasso.get().load(downloadUrl).resize(800, 600).centerCrop().into(holder.img);
        //Log.e("Test",downloadUrl);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_item,viewGroup,false);
        return new ViewHolder(v);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView dateDescription;

        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.image_item_ImageView);
            dateDescription= view.findViewById(R.id.image_item_Description);
            //textDescription = view.findViewById(R.id.image_item_Description);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("test");
                    Log.e("Test","Name clicked : "+getAdapterPosition());
                }
            });

        }

        public ImageView getImg() {
            return img;
        }
        public TextView getTextDescription(){return dateDescription;}
    }



}

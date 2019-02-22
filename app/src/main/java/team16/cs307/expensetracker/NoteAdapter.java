package team16.cs307.expensetracker;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private ArrayList<String> urls;
    private Context context;


    public NoteAdapter(Context context) {
        urls = new ArrayList<String>();
        this.context = context;
    }

    @Override
    public void onBindViewHolder(NoteAdapter.ViewHolder holder, int positionl) {
        Picasso.get().load(urls.get(positionl)).resize(2500, 2500).centerCrop().into(holder.img);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_item,viewGroup,false);

        return new ViewHolder(v);
    }
    @Override
    public int getItemCount() {
        return urls.size();
    }

    public void setUrls (@NonNull ArrayList<String> urlList) {
        /*if (urls != null) {
            urls.clear();
        }*/
        urls.addAll(urlList);
        this.notifyItemRangeInserted(0,urlList.size() - 1);
        System.out.println(urlList);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;

        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.image_item_ImageView);

        }

        public ImageView getImg() {
            return img;
        }
    }

}

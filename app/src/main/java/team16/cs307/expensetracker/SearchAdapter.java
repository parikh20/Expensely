package team16.cs307.expensetracker;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchAdapter extends FirestoreRecyclerAdapter<SearchItem, SearchAdapter.ViewHolder> {
    public SearchAdapter(@NonNull FirestoreRecyclerOptions<SearchItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position, @NonNull SearchItem model) {
        holder.Description.setText(model.getName());
        holder.Description1.setText("$ "+Double.toString(model.getAmount()));
        holder.Description2.setText("Location:"+model.getLocation());

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_item,viewGroup,false);
        return new ViewHolder(v);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView Description;
        private TextView Description1;
        private TextView Description2;



        public ViewHolder(View view) {
            super(view);

            Description = view.findViewById(R.id.Search_Description);
            Description1 = view.findViewById(R.id.Search_amount);
            Description2 = view.findViewById(R.id.Search_location);


        }
    }
}


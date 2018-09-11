package com.kassaiweb.ibiza;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kassaiweb.ibiza.Poll.Poll;
import com.kassaiweb.ibiza.Util.NotificationUtil;

import static android.content.Context.MODE_PRIVATE;

public class ShoppingListFragment extends Fragment {

    private String userId;

    private RecyclerView mRecyclerView;
    private ShoppingAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private EditText newItem;
    private CheckBox notificationCheckbox;
    private Button sendButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {

        mRecyclerView = view.findViewById(R.id.shopping_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        SharedPreferences prefs = getActivity().getSharedPreferences(Constant.APP_NAME, MODE_PRIVATE);
        userId = prefs.getString(Constant.USERID, null);

        mAdapter = new ShoppingAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        newItem = view.findViewById(R.id.shopping_new);
        notificationCheckbox = view.findViewById(R.id.shopping_notification);
        sendButton = view.findViewById(R.id.shopping_send);

        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (newItem.getText()==null || newItem.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Tétel megadása kötelező", Toast.LENGTH_LONG).show();
                    return;
                }

                if (notificationCheckbox.isChecked()) {
                    NotificationUtil.sendNotification("Bevásárlólista", newItem.getText().toString(), userId);
                }


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference shoppingRef = database.getReference("shopping").push();

                ShoppingItem item = new ShoppingItem(shoppingRef.getKey(), newItem.getText().toString(), userId);
                shoppingRef.setValue(item);
                newItem.setText("");


            }
        });



        /*view.findViewById(R.id.cost_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity activity = (MainActivity) getActivity();
                activity.replaceFragment(new CostCreateFragment());

            }
        });*/

    }

}

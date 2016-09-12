package com.v1.firebase.scholarplus.ui.scholarlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.model.User;
import com.v1.firebase.scholarplus.utils.Constants;

import java.util.ArrayList;

/**
 * Created by MacBook on 8/29/16.
 */
public class FilterDialogFragment extends DialogFragment {

    private DatabaseReference mFirebaseDatabaseReference;
    private Spinner regionSpinner,studySpinner;
    private Button advSearch;
    private EditText edtSearch,edtIpk,edtSemester;
    private LinearLayout actionAdvSearch;
    private ArrayList<String> regionList = new ArrayList<>();
    private ArrayList<String> studyList = new ArrayList<>();
    private int advToggle = 0;
    private ScholarListFragment mClass;
    private String mSort;

    public static FilterDialogFragment newInstance(String a) {
        FilterDialogFragment addListDialogFragment = new FilterDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_ASAL,a);
        addListDialogFragment.setArguments(bundle);
        return addListDialogFragment;
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSort = getArguments().getString(Constants.KEY_ASAL);
        }

    }

    /**
     * Open the keyboard automatically when the dialog fragment is opened
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.KEY_USERS);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_filter, null);
//        regionSpinner = (Spinner) rootView.findViewById(R.id.spinner_region);
//        studySpinner = (Spinner) rootView.findViewById(R.id.spinner_majorstudy);
        edtSearch = (EditText) rootView.findViewById(R.id.edit_name);
        edtIpk = (EditText) rootView.findViewById(R.id.edit_ipk);
        edtSemester = (EditText) rootView.findViewById(R.id.edit_semester);
        actionAdvSearch = (LinearLayout) rootView.findViewById(R.id.action_advSearch);
        advSearch = (Button) rootView.findViewById(R.id.adv_search);

        advSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (advToggle == 0){
                    actionAdvSearch.setVisibility(View.VISIBLE);
                    advToggle = 1;
                }
                else{
                    actionAdvSearch.setVisibility(View.GONE);
                    advToggle = 0;
                }
            }
        });
//
//        mFirebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                fetchData(dataSnapshot);
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                fetchData(dataSnapshot);
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                fetchData(dataSnapshot);
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                fetchData(dataSnapshot);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        setAdapter();
//        mEditTextListName = (EditText) rootView.findViewById(R.id.edit_text_list_name);

        /**
         * Call addShoppingList() when user taps "Done" keyboard action
         */
//        mEditTextListName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
//                    addFilter();
//                }
//                return true;
//            }
//        });

        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout*/
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ArrayList<String> input = new ArrayList<String>();
                        input.add(edtSearch.getText().toString());
                        input.add(edtIpk.getText().toString());
                        input.add(edtSemester.getText().toString());
                        Log.d("test",edtSearch.getText().toString());
                        Log.d("test",edtIpk.getText().toString());
                        Log.d("test",edtSemester.getText().toString());

                        Intent data = new Intent();
                        data.putExtra("input",input);

                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
                    }
                });

        return builder.create();
    }

    /**
     * Add new active list
     */
//    public void addFilter() {
//        ScholarListFragment a = new ScholarListFragment();
//        a.orderSearch();
//
//    }
    private void fetchData(DataSnapshot dataSnapshot){
//        studyList.clear();
//        regionList.clear();
        User data = dataSnapshot.getValue(User.class);
        if (!data.getCity().isEmpty() && !regionList.contains(data.getCity()))
            regionList.add(data.getCity());
        if (!data.getJurusan().isEmpty() && !studyList.contains(data.getJurusan()))
            studyList.add(data.getJurusan());
//        if (!regionList.contains(data.getCity()))
//            regionList.add(data.getCity());
//        if (!studyList.contains(data.getJurusan()))
//            studyList.add(data.getJurusan());
        for (DataSnapshot ds:dataSnapshot.getChildren()){
//            User data = ds.getValue(User.class);
//            if (!regionList.contains(data.getCity()))
//                regionList.add(data.getCity());
//            if (!studyList.contains(data.getJurusan()))
//                studyList.add(data.getJurusan());
        }

    }
//    private void setAdapter(){
//
//        regionSpinner.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1,regionList));
//        studySpinner.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1,studyList));
//
//    }
}

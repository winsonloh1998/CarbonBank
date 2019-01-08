package com.cb.carbonbank;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class RewardFragment extends Fragment{
    private static final String TAG = "getRewardID";
    private static final String rewardIDPref = "theRewardID";
    private SharedPreferences sharedPreferences;
    private ListView listViewRewards;
    private List<Rewards> rewardsList;
    private ProgressDialog pDialog;
    private static String GET_URL = "https://crocodilian-trade.000webhostapp.com/SelectRewards.php";
    RequestQueue queue;
    public String selectedReward;


    public RewardFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view;
        view = inflater.inflate(R.layout.fragment_reward, container, false);

        listViewRewards = view.findViewById(R.id.listViewReward);
        pDialog = new ProgressDialog(getActivity());
        rewardsList = new ArrayList<>();

        downloadCourse(getActivity(),GET_URL);

        listViewRewards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        position++;
                        selectedReward = "R000"+position;
                        sharedPreferences = getActivity().getSharedPreferences(rewardIDPref,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String rewardID = selectedReward;
                        editor.putString("rewardID",rewardID);
                        editor.commit();
                    Intent intent=new Intent(getActivity(),RewardDetailActivity.class);
                    startActivity(intent);
            }
        });
        return view;
    }

    private void downloadCourse(Context context, String url) {
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        if (!pDialog.isShowing())
            pDialog.setMessage("Preparing your reward...");
        pDialog.show();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            rewardsList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject courseResponse = (JSONObject) response.get(i);
                                String rewardTitle = courseResponse.getString("RewardTitle");
                                String rewardImage = courseResponse.getString("RewardImage");
                                int ccRequired = Integer.parseInt(courseResponse.getString("CCRequired"));
                                Rewards rewards = new Rewards(rewardTitle, rewardImage, ccRequired);
                                rewardsList.add(rewards);
                            }
                            loadCourse();
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "Error" + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                    }
                });

        // Set the tag on the request.
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void loadCourse(){
        final RewardAdapter adapter = new RewardAdapter(getActivity(), R.layout.fragment_reward, rewardsList);
        listViewRewards.setAdapter(adapter);
    }

}

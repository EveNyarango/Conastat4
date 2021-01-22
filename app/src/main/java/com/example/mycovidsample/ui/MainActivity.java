package com.example.mycovidsample.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycovidsample.R;
import com.example.mycovidsample.adapter.CountryAdapter;
import com.example.mycovidsample.models.CountriesResponse;
import com.example.mycovidsample.Network.CoronaApi;
import com.example.mycovidsample.Network.CoronaService;
import com.google.firebase.auth.FirebaseAuth;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private SearchView searchView;
    private RecyclerView recyclerView;
    private CountryAdapter countryAdapter;
    private List<CountriesResponse> countriesResponseList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.rvCountry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        countryAdapter = new CountryAdapter();
        recyclerView.setAdapter(countryAdapter);

        countriesResponseList = new ArrayList<>();

        CoronaService coronaService =
                CoronaApi.getRetrofitInstance().create(CoronaService.class);


        Call<List<CountriesResponse>> call = coronaService.getCountries();
        call.enqueue(new Callback<List<CountriesResponse>>() {
            @Override
            public void onResponse(Call<List<CountriesResponse>> call, Response<List<CountriesResponse>> response) {

                countriesResponseList = response.body();


                if (countriesResponseList != null) {
                    for (CountriesResponse countriesResponse : countriesResponseList) {

                        System.out.println("Country Name : " + countriesResponse.getCountry() + " - Death Count : " + countriesResponse.getDeaths() + "\n");

                        countryAdapter.setCountryList(getApplicationContext(), countriesResponseList);


                    }
                }


            }

            @Override
            public void onFailure(Call<List<CountriesResponse>> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                countryAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                countryAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search);
        if (id == R.id.action_logout){
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }


}


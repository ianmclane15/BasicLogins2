package com.example.basiclogins;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;

public class RestaurantListActivity extends AppCompatActivity {

    private ListView listViewRestaurant;
    private FloatingActionButton floatingActionButtonAddRestaurant;
    public static final String EXTRA_RESTAURANT = "restaurant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        wireWidgets();

        floatingActionButtonAddRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantListActivity.this, RestaurantActivity.class);
                startActivity(intent);
            }
        });
        populateListView();
        registerForContextMenu(listViewRestaurant);

    }

    private void populateListView() {
        //refactor to only get the items that belong to the user
        //get the current user's objectId (hint: use Backendless.UserService
        //make a dataquery and use the advanced object retrieval pattern
        //to find all restaurants whose ownerId matches th user's objectId
        //sample WHERE clause with a string: name = 'Joe'

        String ownerId = Backendless.UserService.CurrentUser().getObjectId();
        String whereClause = "objectId = '" + ownerId + "'"; //this line isn't complete
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause( whereClause );


        Backendless.Data.of( Restaurant.class).find(new AsyncCallback<List<Restaurant>>() {
            @Override
            public void handleResponse(final List<Restaurant> restaurantList) {
                // all Restaurant instances have been found

                Log.d("LISTACTIVITY", "handleResponse: " + restaurantList.toString());

                RestaurantAdapter adapter = new RestaurantAdapter(
                        RestaurantListActivity.this,
                        android.R.layout.simple_list_item_1,
                        restaurantList);
                listViewRestaurant.setAdapter(adapter);
                //set the onItemClickListener to open the Restaurant Activity
                listViewRestaurant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent restaurantDetailIntent = new Intent(RestaurantListActivity.this, RestaurantActivity.class);
                        restaurantDetailIntent.putExtra(EXTRA_RESTAURANT, restaurantList.get(position));
                        startActivity(restaurantDetailIntent);
                        deleteRestaurant(restaurantList.get(position));
                        finish();

                    }
                });
                //take the clicked object and include it in the Intent
                //in the RestaurantActivity's onCreate, check it there is a Parcelable extra
                //if there is, then get the Restaurant object and populate the fields

            }

            @Override
            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(RestaurantListActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }

            });
    }

            public void deleteRestaurant(Restaurant restaurant) {
                Backendless.Persistence.of(Restaurant.class ).remove(restaurant, new AsyncCallback<Long>()
                {
                    public void handleResponse( Long response )
                    {
                        // Contact has been deleted. The response is the
                        // time in milliseconds when the object was deleted
                        populateListView();
                    }
                    public void handleFault( BackendlessFault fault )
                    {
                        // an error has occurred, the error code can be
                        // retrieved with fault.getCode()
                        Toast.makeText(RestaurantListActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } );
            }

            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
                super.onCreateContextMenu(menu, v, menuInfo);
                MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.menu, menu);
            }

            //context menu_delete stuff
            public boolean onContextItemSelected(MenuItem item) {
                //find out which menu_delete item was pressed
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                switch (item.getItemId()) {
                    case R.id.option1:
                        Restaurant restaurant = (Restaurant) listViewRestaurant.getItemAtPosition(info.position);
                        deleteRestaurant(restaurant);
                        return true;
                    default:
                        return false;
                }
            }




    private void wireWidgets() {

        listViewRestaurant = findViewById(R.id.listview_restaurantlist);
        floatingActionButtonAddRestaurant = findViewById(R.id.button_restaurantlist_newrestaurant);
    }
}

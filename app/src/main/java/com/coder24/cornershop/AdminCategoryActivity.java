package com.coder24.cornershop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView tShirts, sportsTshirts, femaleDresses, sweaters;
    private ImageView glasses, bags, hats, shoes;
    private ImageView headPhones, laptops, watches, mobiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        tShirts = (ImageView) findViewById(R.id.t_Shirts);
        sportsTshirts = (ImageView) findViewById(R.id.sports_t_Shirts);
        femaleDresses = (ImageView) findViewById(R.id.female_dresses);
        sweaters = (ImageView) findViewById(R.id.sweaters);

        glasses = (ImageView) findViewById(R.id.glasses);
        bags = (ImageView) findViewById(R.id.purses_bags);
        hats = (ImageView) findViewById(R.id.hats);
        shoes = (ImageView) findViewById(R.id.shoess);

        headPhones = (ImageView) findViewById(R.id.headphoness);
        laptops = (ImageView) findViewById(R.id.laptops);
        watches = (ImageView) findViewById(R.id.watches);
        mobiles = (ImageView) findViewById(R.id.mobiles);

    }

    private void createIntent(String typeCategory){
        Intent intent  = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
        intent.putExtra("category",typeCategory);
        startActivity(intent);
    }

    public void clickTshirts(View view) {
        createIntent("tShirts");
    }

    public void clickSportsTshirts(View view) {
        createIntent("sportsTshirts");
    }

    public void clickFemaleDresses(View view) {
        createIntent("femaleDresses");
    }

    public void clickSweaters(View view) {
        createIntent("sweaters");
    }

    public void clickGlasses(View view) {
        createIntent("glasses");
    }

    public void clickBags(View view) {
        createIntent("bags");
    }

    public void clickHats(View view) {
        createIntent("hats");
    }

    public void clickShoes(View view) {
        createIntent("shoes");
    }

    public void clickHeadphones(View view) {
        createIntent("headPhones");
    }

    public void clickLaptops(View view) {
        createIntent("laptops");
    }

    public void clickWatches(View view) {
        createIntent("watches");
    }

    public void clickMobiles(View view) {
        createIntent("mobiles");
    }
}

package com.titfer.app;

import com.titfer.Models.CatModel;
import com.titfer.R;

import java.util.ArrayList;

/**
 * Created by sotra on 7/13/2017.
 */

public class Constants {

    public  static  String  Ref = "https://titfer-ecffc.firebaseio.com/" ;
    public  static  String  RefUses = Ref+"Users" ;


    public static ArrayList<CatModel> searchCats(){
        ArrayList <CatModel> catModels = new ArrayList<>() ;

        catModels.add(new CatModel("Fashion Brands" , R.drawable.fashion)) ;
        catModels.add(new CatModel("Fashion Designers" , R.drawable.fashion2)) ;
        catModels.add(new CatModel("Jewelry" , R.drawable.neck)) ;
        catModels.add(new CatModel("Hairdressers" , R.drawable.hairdressers)) ;
        catModels.add(new CatModel("Makeup Artists" , R.drawable.brush)) ;
        catModels.add(new CatModel("Professionals" , R.drawable.professional)) ;
        return catModels ;
    }



    public static ArrayList<CatModel> searchSubs(){
        ArrayList <CatModel> catModels = new ArrayList<>() ;

        catModels.add(new CatModel("Couture" , R.drawable.couture)) ;
        catModels.add(new CatModel("Ready to wear" , R.drawable.ready)) ;
        catModels.add(new CatModel("Ethnic" , R.drawable.ethinic)) ;
        catModels.add(new CatModel("Shoes and bags" , R.drawable.bags)) ;
        catModels.add(new CatModel("Wedding" , R.drawable.weeding)) ;
        catModels.add(new CatModel("Abaya" , R.drawable.abaya)) ;
        return catModels ;
    }


}

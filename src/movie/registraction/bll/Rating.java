/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movie.registraction.bll;

import movie.registraction.be.Movie;

/**
 *
 * @author B
 */
public class Rating {
    
    private boolean half;
    private int wholeNumber;


    public void initRating(double rating, String ratingType)
    {
        if(ratingType.equals("imdb")){
            if(rating-Math.floor(rating) < 0.75){


                if(rating-Math.floor(rating) > 0.25)
                {
                    half = true;
                }

                wholeNumber = (int) Math.floor(rating); 

            }
            else
            {
                wholeNumber = (int) Math.ceil(rating);
            }
        }
        else
        {
            wholeNumber = (int) rating;
        }
        
        setRatingStars();
    }
    
    
    private void setRatingStars()
    {
  
        for(int i = 1; i < 11; i++)
        {
            if(i <= wholeNumber)
            {
                System.out.println("*"+i);
            }
            else if(i == wholeNumber+1 && half == true)
            {
                System.out.println("half");
            }
            else
            {
                System.out.println("-"+i);
            }
            
        }
        
    }
    
    private void onMouseOver(){
        int thisIndex = 0;
        for(int i = 0; i < 10; i++){
           if(i <= thisIndex){
              //star marked

           }else{

               //star grey
           }
       }
    }
         
}

package com.mahout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class Main
{
    class UserBasedRecommenderBuilder implements RecommenderBuilder
    {
        public Recommender buildRecommender(DataModel model) throws TasteException
        {
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
            return new GenericUserBasedRecommender(model, neighborhood, similarity);
        }
        
    }
    
    class ItemBasedRecommenderBuilder implements RecommenderBuilder
    {
        public Recommender buildRecommender(DataModel model) throws TasteException
        {
            ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
            return new GenericItemBasedRecommender(model, similarity);
        }
     
    }
    
    public void SimpleTestFoo()
    {
        try
        {
            DataModel dataModel = new FileDataModel(new File("uMahout.data"));
            // test for user based cf
            UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, dataModel);
            UserBasedRecommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
            ArrayList<RecommendedItem> recomendations = (ArrayList<RecommendedItem>) recommender.recommend(2, 3);
            for (RecommendedItem recommendedItem : recomendations)
            {
                System.out.println(recommendedItem);
            }

            // test for item based cf
            ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(dataModel);
            ItemBasedRecommender recommenderItemBased = new GenericItemBasedRecommender(dataModel, itemSimilarity);
            ArrayList<RecommendedItem> recommendedItems = (ArrayList<RecommendedItem>) recommenderItemBased.recommend(2, 3);
            for (RecommendedItem recommendedItem : recommendedItems)
            {
                System.out.println(recommendedItem);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void CFTestWithEvaluationFoo()
    {
        //test the resulte of cf
        try
        {
            DataModel dataModel = new FileDataModel(new File("uMahout.data"));
            RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
            RecommenderBuilder builder = new UserBasedRecommenderBuilder();
            double result = evaluator.evaluate(builder, null, dataModel, 0.9, 0.1);
            System.out.println(result);
            
            RecommenderBuilder builder2 = new ItemBasedRecommenderBuilder();
            result = evaluator.evaluate(builder2, null, dataModel, 0.9, 0.1);
            System.out.println(result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        Main mainObject = new Main();
        mainObject.SimpleTestFoo();
        mainObject.CFTestWithEvaluationFoo();
    }
}

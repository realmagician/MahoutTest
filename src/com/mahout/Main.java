package com.mahout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.PreferenceInferrer;
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
            System.out.println(dataModel.getMaxPreference());
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
        // test the result of cf
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

    public void ContentBasedFoo()
    {
        try
        {
            DataModel model = new FileDataModel(new File("test_no_preference"));
            UserSimilarity similarity = new EuclideanDistanceSimilarity(model);
            // PreferenceArray array = model.getPreferencesFromUser(1);
            // for(int i=0;i<array.length();i++)
            // {
            // System.out.println(array.getItemID(i)+" "+array.getValue(i));
            // }
            similarity.setPreferenceInferrer(new PreferenceInferrer()
            {
                public void refresh(Collection<Refreshable> alreadyRefreshed)
                {
                }

                public float inferPreference(long userID, long itemID) throws TasteException
                {
                    // set 0 if there is no prefernce from userID to itemID,
                    // or UserSimilarity will ignore the difference
                    // between userID and itemID
                    return 0;
                }
            });

            double temp = similarity.userSimilarity(1, 1);
            System.out.println(temp);
            temp = similarity.userSimilarity(1, 2);
            System.out.println(temp);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        Main mainObject = new Main();
        // mainObject.SimpleTestFoo();
        // mainObject.CFTestWithEvaluationFoo();
        mainObject.ContentBasedFoo();
    }
}

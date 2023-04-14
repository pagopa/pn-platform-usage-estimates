package it.pagopa.pn.platform.utils;


import it.pagopa.pn.platform.mapper.EstimateMapper;
import it.pagopa.pn.platform.middleware.db.dao.EstimateDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

@Component
public class TimelineGenerator {

    @Autowired
    private static EstimateDAO estimateDAO;

    //dati estratti dal db
    private static List<PnEstimate> dbList ;

    //output
    private static List<PnEstimate> timelineList = new ArrayList<>();

    public static List<PnEstimate> extractAllEstimates(String paId) {
        //fare get ad external registries per prendere data di onboarding
        //calcolare data di inserimento (data di onboarding + 30gg)
        //calcolare data scadenza
        //fare get all e salvare dati db dentro dbList (mesi)
        //fare controlli per popolare timelineList (controlli tra data inserimento e data scadenza)

        return null;
    }

    //metodo per generare mesi missing
    public static void MissingGenerator (){
        //1 caso -> inserire i mesi missing prima del PRIMO inserimento a db
        //onboarding -> 10/01/23
        //data inserimento (+30gg) -> 10/02/23
        //prima data inserita a db -> 04/05/23


        //int first = data inserimento; -> 02
        //int last = prima data inserita a db (dbList[0] o dbList[last] in base a ordinamento) -> 05
        //      i = 03; i < 05; i++
        // for (int i = first + 1; i < last; i++)
        //      timelineList.add(i) -> aggiunta mesi(vedere 0 davanti ????)

    }


}

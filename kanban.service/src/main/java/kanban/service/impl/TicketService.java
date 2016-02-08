package kanban.service.impl;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import kanban.db.entity.*;
import kanban.service.contract.IMongoService;
import kanban.service.contract.ITicketService;
import kanban.ui.entity.UploadTicket;
import kanban.utils.callback.Async;
import kanban.utils.callback.MongoCallBack;
import kanban.utils.callback.RunnableFunction;

import javax.inject.Inject;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by S0089075 on 28/01/2016.
 */
public class TicketService implements ITicketService {

    @Inject
    private IMongoService mongoService;

    @Override
    public MongoCallBack<String> uploadTicket(String toUpload){
        MongoCallBack<String> callbackResult = new MongoCallBack<>();

        Vertx.currentContext().runOnContext(v -> {

            List result = Json.decodeValue(getUploadString(toUpload),List.class);

            Consumer<UploadTicket> run = t -> {
                Async.When(() -> mongoService.findOne(PriorityParameter.class,this.query("libelle",t.getPriorite())))
                        .Rule(x -> x != null)
                        .Otherwise(x -> callbackResult.finish("NOK PriorityParameter -> " + t.getPriorite()))
                        .doThat(priority -> {

                            Async.When(() -> mongoService.findOne(StatutParameter.class, this.query("libelle",t.getEtat())))
                                    .Rule(x -> x != null)
                                    .Otherwise(x -> callbackResult.finish("NOK StatutParameter-> " + t.getEtat()))
                                    .doThat(statut -> {
                                        Async.When(() -> mongoService.findOne(User.class, queryForUser(t.getIntervenant())))
                                                .Rule(x -> x != null)
                                                .Otherwise(x -> callbackResult.finish("NOK User -> " + t.getIntervenant()))
                                                .doThat(user -> {
                                                    Async.When(() -> mongoService.getNextSequence(Ticket.class))
                                                            .doThat(index -> {
                                                                Ticket ticket = new Ticket();
                                                                ticket.setStatut(new ParamColorTuple(statut));
                                                                ticket.setPriority(new ParamColorTuple(priority));
                                                                ticket.setReference(trimReference(t.getIncident()));
                                                                ticket.setOwner(new ParamTuple(user.getLogin(),t.getIntervenant()));
                                                                ticket.setCaisse(libelleCaisseToNumber(t.getEtablissement()));
                                                                ticket.setSummary(t.getResume());
                                                                ticket.setZone(((statut.getCode().equals("AFFECTE"))?new ParamTuple("BackLog","BackLog"): new ParamTuple("Analyse","Analyse")));
                                                                ticket.set_id(index.toString());
                                                                mongoService.insert(ticket);
                                                            });
                                                });


                                    });
                        });
            };


            for (Object o : result){
                UploadTicket uploadTicket = Json.decodeValue(new JsonObject((LinkedHashMap)o).encode(),UploadTicket.class);
                run.accept(uploadTicket);

            }

            callbackResult.finish("done");


        });

        return callbackResult;

    }

    private JsonObject queryForUser(String fullname){
        String[] splitName = fullname.split(" ");
        return query("firstName",splitName[0].trim()).put("lastName",splitName[1].trim());
    }

    private JsonObject query(String key, String value){ return new JsonObject().put(key,value);}

    private String trimReference(String originalRef) {
        return String.valueOf(Integer.parseInt(originalRef.substring(3)));
    }

    private String getUploadString(String toUpload) {
        File f = new File(toUpload);
        String result = null;
        try (InputStream reader = new FileInputStream(f)){

            byte[] b = new byte[reader.available()];
            reader.read(b);
            result = new String(b);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Map<String,String> map = null;
    private String libelleCaisseToNumber(String libelle){

        if (map == null) {
            map = new HashMap<>();
            map.put("Banque de la Réunion","BR");
            map.put("BPCE","BPCE");

            map.put("Caisse d'Epargne Provence-Alpes-Corse",        "11315");
            map.put("Caisse d'Epargne Normandie",                   "11425");
            map.put("Caisse d'Epargne de Midi-Pyrénées",            "13135");
            map.put("Caisse d'Epargne du Languedoc Roussillon",     "13485"); // verifier
            map.put("Caisse d'Epargne Bretagne-Pays de Loire",      "14445");
            map.put("Caisse d'Epargne Loire-Centre",                "14505");
            map.put("Caisse d'Epargne Nord France Europe",          "16275");
            map.put("Caisse d'Epargne de Picardie",                 "18025");
            map.put("Caisse d'Epargne de Côte d'Azur",              "18315");
            map.put("Caisse d'Epargne d'Auvergne et du Limousin",   "18715");
            map.put("Caisse d'Epargne de Bourgogne Franche-Comté",  "12135");
            map.put("Banque BCP",                                   "BCP[12579]");
            map.put("Caisse d'Epargne Aquitaine Poitou-Charentes",  "13335");
            map.put("Caisse d'Epargne Rhône Alpes",                 "13825");
            map.put("Caisse d'Epargne Loire Drome ardeche",         "14265"); // vérifier
            map.put("Caisse d'Epargne Lorraine Champagne-Ardenne",  "15135");
            map.put("Caisse d'Epargne d'Alsace",                    "16705"); // vérifier
            map.put("Caisse d'Epargne Ile-de-France",               "17515");
            map.put("CRC",                                          "CRC");
            map.put("IT-CE",                                        "IT-CE");
            map.put("",                                             "NA");
        }

        return (map.containsKey(libelle)) ? map.get(libelle): libelle;
    }


}

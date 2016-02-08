package kanban.db.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import kanban.json.JsonDateDeserializer;
import kanban.json.JsonDateSerializer;

import java.util.Date;

/**
 * Created by S0089075 on 26/01/2016.
 */
public class VersionParameter extends AbstractArchivableParameter {


    //@JsonSerialize(using = JsonDateSerializer.class)
    //@JsonDeserialize(using = JsonDateDeserializer.class)
    private Date dateVfo;
    //@JsonSerialize(using = JsonDateSerializer.class)
    //@JsonDeserialize(using = JsonDateDeserializer.class)
    private Date dateUti;
    //@JsonSerialize(using = JsonDateSerializer.class)
    //@JsonDeserialize(using = JsonDateDeserializer.class)
    private Date datePvUti;
    //@JsonSerialize(using = JsonDateSerializer.class)
    //@JsonDeserialize(using = JsonDateDeserializer.class)
    private Date dateQpa;
    //@JsonSerialize(using = JsonDateSerializer.class)
    //@JsonDeserialize(using = JsonDateDeserializer.class)
    private Date datePvQpa;
    //@JsonSerialize(using = JsonDateSerializer.class)
    //@JsonDeserialize(using = JsonDateDeserializer.class)
    private Date dateProd;

    private String colorPvUtiToLate;
    private String colorPvQpaToLate;




    public VersionParameter() {
        super();
        this.setKanbanParameter(KanbanParameter.EXPOSED_ID);
    }


    public String getColorPvQpaToLate() {
        return colorPvQpaToLate;
    }

    public void setColorPvQpaToLate(String colorPvQpaToLate) {
        this.colorPvQpaToLate = colorPvQpaToLate;
    }

    public String getColorPvUtiToLate() {
        return colorPvUtiToLate;
    }

    public void setColorPvUtiToLate(String colorPvUtiToLate) {
        this.colorPvUtiToLate = colorPvUtiToLate;
    }



    public Date getDateProd() {
        return dateProd;
    }

    public void setDateProd(Date dateProd) {
        this.dateProd = dateProd;
    }

    public Date getDatePvQpa() {
        return datePvQpa;
    }

    public void setDatePvQpa(Date datePvQpa) {
        this.datePvQpa = datePvQpa;
    }

    public Date getDateQpa() {
        return dateQpa;
    }

    public void setDateQpa(Date dateQpa) {
        this.dateQpa = dateQpa;
    }

    public Date getDatePvUti() {
        return datePvUti;
    }

    public void setDatePvUti(Date datePvUti) {
        this.datePvUti = datePvUti;
    }

    public Date getDateUti() {
        return dateUti;
    }

    public void setDateUti(Date dateUti) {
        this.dateUti = dateUti;
    }

    public Date getDateVfo() {
        return dateVfo;
    }

    public void setDateVfo(Date dateVfo) {
        this.dateVfo = dateVfo;
    }


}

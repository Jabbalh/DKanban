package kanban.db.entity;

/**
 * Created by S0089075 on 27/01/2016.
 */
public class ArchivableParamTuple extends ParamTuple {

    private Boolean archive;


    public ArchivableParamTuple(String code, String libelle, Boolean archive){
        super(code,libelle);
        this.archive = archive;
    }

    public <T extends AbstractArchivableParameter> ArchivableParamTuple(T value){
        this(value.getCode(), value.getLibelle(), value.getArchive());
    }

    public ArchivableParamTuple() {

    }

    public Boolean getArchive() {
        return archive;
    }

    public void setArchive(Boolean archive) {
        this.archive = archive;
    }
}

package kanban.db.entity;

/**
 * Created by S0089075 on 27/01/2016.
 */
public class AbstractArchivableParameter extends AbstractParameter {
    private Boolean archive;

    public Boolean getArchive() {
        return archive;
    }

    public void setArchive(Boolean archive) {
        this.archive = archive;
    }
}

package kanban.db.entity;

/**
 * Created by S0089075 on 29/01/2016.
 */
public class MongoSequence {
    private String _id;
    private Integer seq;

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}

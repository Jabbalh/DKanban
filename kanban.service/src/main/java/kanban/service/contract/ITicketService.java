package kanban.service.contract;

import kanban.db.entity.Ticket;
import kanban.ui.entity.UploadTicket;
import kanban.utils.callback.MongoCallBack;

import java.util.List;

/**
 * Created by S0089075 on 28/01/2016.
 */
public interface ITicketService {

    MongoCallBack<String> uploadTicket(String toUpload);
}

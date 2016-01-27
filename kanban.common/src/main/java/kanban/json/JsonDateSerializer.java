package kanban.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by S0089075 on 26/01/2016.
 */
public class JsonDateSerializer extends JsonSerializer<Date> {


    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("\"yyyy-MM-dd'T'HH:mm:ss'.000Z'\"");
    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        try{
            String formattedDate = dateFormat.format(date);
            gen.writeString(formattedDate);
        } catch (Exception ex) {
            gen.writeString(dateFormat.format(new Date()));
        }

    }

}

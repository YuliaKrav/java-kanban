package formatter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(localDateTime.format(DATE_TIME_FORMATTER));
        }
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String dateTime = jsonReader.nextString();
        if ("null".equals(dateTime)) return null;
        return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
    }
}

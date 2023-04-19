package integrationTests.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dtos.SportDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CastToJson {

    public String toJsonFormat(Object object) throws JsonProcessingException {
        ObjectMapper jsonMapper = new ObjectMapper();
        return jsonMapper.writeValueAsString(object);
    }
}

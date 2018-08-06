package com.antkorwin.junit5integrationtestutils.mvc.requester;

import com.antkorwin.junit5integrationtestutils.mvc.PrimitiveConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created on 03.08.2018.
 *
 * Билдер для обработки результата запроса к Mvc контроллеру
 *
 * @author Sergey Vdovin
 * @author Korovin Anatoliy
 */
public class MvcRequestResult {

    private final ResultActions resultActions;
    private final ObjectMapper jsonMapper;

    MvcRequestResult(ResultActions resultActions, ObjectMapper jsonMapper) {
        this.resultActions = resultActions;
        this.jsonMapper = jsonMapper;
    }

    /**
     * Выполнить указанный ассерт над результатом запроса
     *
     * @param matcher ассерт выполняемый над результатом запроса
     */
    public MvcRequestResult doExpect(ResultMatcher matcher) throws Exception {
        resultActions.andDo(print());
        resultActions.andExpect(matcher);
        return this;
    }

    /**
     * check status code of the response
     *
     * @param status expected status code
     */
    public MvcRequestResult expectStatus(HttpStatus status) throws Exception {
        resultActions.andDo(print());
        resultActions.andExpect(status().is(status.value()));
        return this;
    }

    /**
     * Позволяет вернуть тело ответа в виде объекта десериализованного из JSON
     * Нужен когда, требуется десериализовать Generic Объекты (CollectionDTO &lt; String &gt;)
     *
     * @param typeReference тип данных в который необходимо конвертировать JSON
     * @param <ResultType>
     */
    public <ResultType> ResultType doReturn(TypeReference<ResultType> typeReference) throws Exception {
        resultActions.andDo(print());
        String body = resultActions.andReturn().getResponse().getContentAsString();

        return isBlank(body) ? null : jsonMapper.readValue(body, typeReference);
    }

    /**
     * Позволяет вернуть тело ответа в виде объекта десериализованного из JSON
     *
     * @param returnType   тип данных в который необходимо конвертировать JSON
     * @param <ResultType>
     */
    public <ResultType> ResultType returnAs(Class<ResultType> returnType) throws Exception {
        resultActions.andDo(print());
        String body = resultActions.andReturn().getResponse().getContentAsString();

        return isBlank(body) ? null : jsonMapper.readerFor(returnType)
                                                .readValue(resultActions.andReturn()
                                                                        .getResponse()
                                                                        .getContentAsString());
    }

    /**
     * return result as a primitive type
     *
     * @param returnType expected type of result
     */
    public <ResultType> ResultType returnAsPrimitive(Class<ResultType> returnType) throws Exception {
        resultActions.andDo(print());
        String body = resultActions.andReturn().getResponse().getContentAsString();
        return isBlank(body) ? null : (ResultType) PrimitiveConverter.convertToPrimitive(body, returnType);
    }
}

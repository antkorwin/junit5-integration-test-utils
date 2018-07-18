package com.antkorwin.junit5integrationtestutils.mvc;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.MimeType;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created on 30.08.2017.
 * хелпер класс для выполнения запросов к mvc Контроллерам
 *
 * @author Sergey Vdovin
 * @author Korovin Anatoliy
 */
public class MvcRequester {

    protected final ObjectMapper sendJsonMapper;
    protected final ObjectMapper receiveJsonMapper;
    private final MockMvc mockMvc;

    private MvcRequester(MockMvc mockMvc) {

        this.mockMvc = mockMvc;
        this.sendJsonMapper = new ObjectMapper();
        this.receiveJsonMapper = new ObjectMapper();
    }

    private MvcRequester(MockMvc mockMvc,
                         ObjectMapper sendJsonMapper,
                         ObjectMapper receiveJsonMapper) {

        this.mockMvc = mockMvc;
        this.sendJsonMapper = sendJsonMapper;
        this.receiveJsonMapper = receiveJsonMapper;
    }

    /**
     * Статик фактори метод, для создания хелпер объекта
     *
     * @param mockMvc mock через который будут выполнятся запросы
     */
    public static MvcRequester on(MockMvc mockMvc) {
        return new MvcRequester(mockMvc);
    }

    /**
     * Статик фактори метод, для создания хелпер объекта, с у казанием мапера,
     * которым будем преобразовывать тело запросов в json перед отправкой и получением
     *
     * @param mockMvc      mockMvc через который выполняем запросы
     * @param objectMapper мапер которым кодируем тело запроса
     *
     * @return
     */
    public static MvcRequester on(MockMvc mockMvc,
                                  ObjectMapper objectMapper) {

        return new MvcRequester(mockMvc, objectMapper, objectMapper);
    }

    /**
     * Статик фактори метод, для создания хелпер объекта, с у казанием мапера,
     * которым будем преобразовывать тело запросов в json перед отправкой
     * и указанием отдельного мапера для получаемого json
     *
     * @param mockMvc           mockMvc через который выполняем запросы
     * @param sendJsonMapper    мапер которым кодируем тело запроса перед отправкой
     * @param receiveJsonMapper мапер которым будет декодироватся приходящий в ответе json
     *
     * @return
     */
    public static MvcRequester on(MockMvc mockMvc,
                                  ObjectMapper sendJsonMapper,
                                  ObjectMapper receiveJsonMapper) {

        return new MvcRequester(mockMvc, sendJsonMapper, receiveJsonMapper);
    }

    /**
     * сеттер для url на который будет отправлен запрос
     *
     * @param pattern щаблон
     * @param args    перменные для вставки в шаблон
     */
    public MvcRequestPointed to(String pattern, Object... args) {
        return new MvcRequestPointed(mockMvc, UriComponentsBuilder.fromUriString(pattern)
                                                                  .buildAndExpand(args)
                                                                  .encode()
                                                                  .toUri(),
                                     sendJsonMapper,
                                     receiveJsonMapper);
    }

    /**
     * Билдер для запросов к MVC контроллерам с предопределенным URL и MockMvc через который буду выполнятся запросы
     */
    public static class MvcRequestPointed {
        private final URI uri;
        private final MockMvc mockMvc;
        private final Multimap<String, String> params;
        private final Map<String, MvcRequestFileData> files;
        private final ObjectMapper sendJsonMapper;
        private final ObjectMapper receiveJsonMapper;
        private final Multimap<String, String> headers;
        private final List<RequestPostProcessor> postProcessors;

        MvcRequestPointed(MockMvc mockMvc,
                          URI uri,
                          ObjectMapper sendJsonMapper,
                          ObjectMapper receiveJsonMapper) {
            this.uri = uri;
            this.mockMvc = mockMvc;
            this.params = ArrayListMultimap.create();
            this.files = new HashMap<>();
            this.sendJsonMapper = sendJsonMapper;
            this.receiveJsonMapper = receiveJsonMapper;
            this.headers = ArrayListMultimap.create();
            this.postProcessors = new ArrayList<>();
        }

        public MvcRequestPointed withParam(String name, Object... values) {
            for (Object value : values) {
                this.params.put(name, String.valueOf(value));
            }
            return this;
        }

        public MvcRequestPointed withHeader(String name, Object... values) {
            for (Object value : values) {
                this.headers.put(name, String.valueOf(value));
            }
            return this;
        }

        /**
         * С OAuth аутентификацией.
         *
         * @param token OAuth-токен.
         */
        public MvcRequestPointed withOAuth(String token) {
            postProcessors.add(OAuthRequestPostProcessor.getInstance(token));
            return this;
        }

        /**
         * С Basic аутентификацией.
         *
         * @param username имя пользователя.
         * @param password пароль пользователя.
         */
        public MvcRequestPointed withBasicAuth(String username, String password) {
            postProcessors.add(httpBasic(username, password));
            return this;
        }

        /**
         * С CSRF.
         */
        public MvcRequestPointed withCsrf() {
            postProcessors.add(csrf());
            return this;
        }

        /**
         * Выполинть POST запрос без параметров
         *
         * @throws Exception
         */
        public MvcRequestResult post() throws Exception {
            return new MvcRequestResult(mockMvc.perform(make(MockMvcRequestBuilders::post)),
                                        receiveJsonMapper);
        }

        /**
         * Добавляем файл для загрузки
         *
         * @param fieldName поле формы в котором будет передан файл
         * @param fileData  массив байт для отправки
         *
         * @return
         */
        public MvcRequestPointed withFile(String fieldName,
                                          String originalFileName,
                                          MimeType mimeType,
                                          byte[] fileData) {

            this.files.put(fieldName,
                           new MvcRequestFileData(originalFileName, mimeType, fileData));

            return this;
        }

        /**
         * выполнение загрузки файла
         *
         * @throws Exception
         */
        public MvcRequestResult upload() throws Exception {
            return new MvcRequestResult(this.mockMvc.perform(makeUpload(null)),
                                        receiveJsonMapper);
        }

        /**
         * Выполнение загрузки файла с авторизацией
         *
         * @param token токен авторизации
         *
         * @return
         * @throws Exception
         */
        public MvcRequestResult uploadWithAuth(String token) throws Exception {
            return new MvcRequestResult(this.mockMvc.perform(makeUpload(token)),
                                        receiveJsonMapper);
        }

        /**
         * Выполнить пост запрос, с отправкой объекта в виде JSON
         *
         * @param content отправляемый объект
         */
        public MvcRequestResult post(Object content) throws Exception {
            return new MvcRequestResult(
                    mockMvc.perform(make(MockMvcRequestBuilders::post)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(sendJsonMapper.writeValueAsString(content))),
                    receiveJsonMapper);
        }

        /**
         * Выполнить PUT запрос, с отправкой объекта в виде JSON
         *
         * @param content отправляемый объект
         */
        public MvcRequestResult put(Object content) throws Exception {
            return new MvcRequestResult(
                    mockMvc.perform(make(MockMvcRequestBuilders::put)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(sendJsonMapper.writeValueAsString(content))),
                    receiveJsonMapper);
        }

        /**
         * Выполнить DELETE запрос, с отправкой объекта в виде JSON
         */
        public MvcRequestResult delete() throws Exception {
            return new MvcRequestResult(
                    mockMvc.perform(make(MockMvcRequestBuilders::put)
                                            .contentType(MediaType.APPLICATION_JSON)),
                    receiveJsonMapper);
        }

        /**
         * Выполнение пост запроса с авторизацией
         *
         * @param content   Объект отправляемый в виде json
         * @param authToken токен авторизации
         *
         * @deprecated вместо этого нужно использовать метод .withOAuth(token:String?) и .post(Content:Object?)
         */
        @Deprecated
        public MvcRequestResult postWithAuth(Object content, String authToken) throws Exception {
            return new MvcRequestResult(
                    mockMvc.perform(makePostWithAuth(authToken)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(this.sendJsonMapper.writeValueAsString(content))),
                    receiveJsonMapper);
        }

        /**
         * Выполнение пост запрса с авторизацией и без отправки какого-либо контента.
         *
         * @param authToken токен авторизации
         *
         * @deprecated вместо этого нужно использовать метод .withOAuth(token:String?) и .post()
         */
        @Deprecated
        public MvcRequestResult postWithAuth(String authToken) throws Exception {
            return new MvcRequestResult(mockMvc.perform(makePostWithAuth(authToken)),
                                        receiveJsonMapper);
        }

        /**
         * Выполнение гет запроса
         */
        public MvcRequestResult get() throws Exception {
            return new MvcRequestResult(mockMvc.perform(make(MockMvcRequestBuilders::get)),
                                        receiveJsonMapper);
        }

        /**
         * Выполнение гет запроса, с отправкой объекта в виде JSON
         *
         * @param content отправляемый объект
         */
        public MvcRequestResult get(Object content) throws Exception {
            return new MvcRequestResult(
                    mockMvc.perform(make(MockMvcRequestBuilders::get)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(sendJsonMapper.writeValueAsString(content))),
                    receiveJsonMapper);
        }

        /**
         * Выполнение гет запроса с авторизацией
         *
         * @deprecated вместо этого нужно использовать метод .withOAuth(token:String?) и .get()
         */
        @Deprecated
        public MvcRequestResult getWithAuth(String authToken) throws Exception {
            return new MvcRequestResult(mockMvc.perform(makeGetWithAuth(authToken)),
                                        receiveJsonMapper);
        }

        private MockHttpServletRequestBuilder make(Function<URI, MockHttpServletRequestBuilder> builderSupplier) {

            MockHttpServletRequestBuilder builder = builderSupplier.apply(uri);
            if (!params.isEmpty()) {
                params.asMap()
                      .forEach((key, values) ->
                                       values.forEach(value ->
                                                              builder.param(key, value)));
            }
            if (!postProcessors.isEmpty()) {
                postProcessors.forEach(builder::with);
            }
            return builder;
        }

        private MockHttpServletRequestBuilder makePostWithAuth(String authToken) {
            return make(MockMvcRequestBuilders::post)
                    .header("Authorization", String.format("Bearer %s", authToken));
        }

        private MockHttpServletRequestBuilder makeGetWithAuth(String authToken) {
            return make(MockMvcRequestBuilders::get)
                    .header("Authorization", String.format("Bearer %s", authToken));
        }

        /**
         * проставляем параметры и заголовки в запрос
         *
         * @param builder запрос
         */
        private MockHttpServletRequestBuilder prepareRequest(MockHttpServletRequestBuilder builder) {
            if (!params.isEmpty()) {
                params.asMap().forEach(
                        (key, values) ->
                                values.forEach(value ->
                                                       builder.param(key, value)));
            }
            if (!headers.isEmpty()) {
                headers.asMap().forEach(
                        (key, values) ->
                                values.forEach(value ->
                                                       builder.header(key, value)));
            }
            return builder;
        }

        /**
         * Создание запроса на загрузку файла
         *
         * @param token токен авторизации
         *
         * @return
         */
        private MockHttpServletRequestBuilder makeUpload(String token) {
            MockMultipartHttpServletRequestBuilder builder = fileUpload(uri);
            if (isNotBlank(token)) {
                builder.header("Authorization", String.format("Bearer %s", token));
            }
            for (Map.Entry<String, MvcRequestFileData> entry : this.files.entrySet()) {
                MvcRequestFileData data = entry.getValue();
                MockMultipartFile mockMultipartFile = new MockMultipartFile(entry.getKey(),
                                                                            data.getOriginalFileName(),
                                                                            data.getMimeType() == null
                                                                            ? null
                                                                            : data.getMimeType().toString(),
                                                                            data.fileData);
                builder.file(mockMultipartFile);
            }
            return prepareRequest(builder);
        }

    }

    /**
     * Билдер для обработки результата запроса к Mvc контроллеру
     */
    public static class MvcRequestResult {
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
            resultActions.andExpect(status().isOk());
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

    public static class MvcRequestFileData {

        private final String originalFileName;
        private final byte[] fileData;
        private final MimeType mimeType;

        public MvcRequestFileData(String originalFileName,
                                  MimeType mimeType,
                                  byte[] fileData) {

            this.originalFileName = originalFileName;
            this.fileData = fileData;
            this.mimeType = mimeType;
        }

        public String getOriginalFileName() {
            return originalFileName;
        }

        public byte[] getFileData() {
            return fileData;
        }

        public MimeType getMimeType() {
            return mimeType;
        }
    }
}
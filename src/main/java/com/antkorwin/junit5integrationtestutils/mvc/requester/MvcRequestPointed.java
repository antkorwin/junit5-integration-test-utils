package com.antkorwin.junit5integrationtestutils.mvc.requester;

import com.antkorwin.junit5integrationtestutils.mvc.OAuthRequestPostProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.MimeType;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;

/**
 * Created on 03.08.2018.
 * <p>
 * Билдер для запросов к MVC контроллерам с предопределенным URL и MockMvc через который буду выполнятся запросы
 *
 * @author Sergey Vdovin
 * @author Korovin Anatoliy
 */
public class MvcRequestPointed {

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
     *
     * @return MvcRequestPointed
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
     *
     * @return MvcRequestPointed
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
     * @return MvcRequestPointed
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
     * @return MvcRequestResult
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
     *
     * @return MvcRequestResult
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
     *
     * @return MvcRequestResult
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
     *
     * @return MvcRequestResult
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
     * @return MvcRequestResult
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
        return prepareRequest(builder);
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
     * set request parameters, headers and postProcessors
     *
     * @param builder request
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
        if (!postProcessors.isEmpty()) {
            postProcessors.forEach(builder::with);
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
                                                                        data.getFileData());
            builder.file(mockMultipartFile);
        }
        return prepareRequest(builder);
    }
}

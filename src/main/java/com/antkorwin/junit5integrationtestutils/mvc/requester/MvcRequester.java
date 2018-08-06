package com.antkorwin.junit5integrationtestutils.mvc.requester;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created on 30.08.2017.
 * хелпер класс для выполнения запросов к mvc Контроллерам
 *
 * @author Sergey Vdovin
 * @author Korovin Anatoliy
 */
public class MvcRequester {

    private final ObjectMapper sendJsonMapper;
    private final ObjectMapper receiveJsonMapper;
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
        String url = pattern.trim();
        if (url.charAt(0) != '/') {
            url = '/' + url;
        }
        return new MvcRequestPointed(mockMvc, UriComponentsBuilder.fromUriString(url)
                                                                  .buildAndExpand(args)
                                                                  .encode()
                                                                  .toUri(),
                                     sendJsonMapper,
                                     receiveJsonMapper);
    }
}
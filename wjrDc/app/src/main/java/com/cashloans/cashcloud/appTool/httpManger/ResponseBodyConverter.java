package com.cashloans.cashcloud.appTool.httpManger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.cashloans.thloans.appTool.BaseBean;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Author: Unknown
 * Date: 2018/08/14
 * Desc:
 */
final class ResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private Gson gson;
    private Type type;

    ResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        try {
            BaseBean baseBean = gson.fromJson(response, BaseBean.class);
            if (!(baseBean.getCode() == 0)) {
                throw new ApiException(baseBean.getCode(), baseBean.getMessage());
            }
            if (type.toString().replace("class ", "").equals(BaseBean.class.getName())) {
                return (T) baseBean;
            }
            return gson.fromJson(baseBean.getData(), type);
        } catch (JsonSyntaxException e) {
            BaseBean baseArrayBean = gson.fromJson(response, BaseBean.class);
            if (!(baseArrayBean.getCode() == 0)) {
                throw new ApiException(baseArrayBean.getCode(), baseArrayBean.getMessage());
            }
            if (type.toString().replace("class ", "").equals(BaseBean.class.getName())) {
                return (T) baseArrayBean;
            }

            return gson.fromJson(baseArrayBean.getData(), type);
        } finally {
            value.close();
        }
    }
}

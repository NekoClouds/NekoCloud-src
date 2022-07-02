package me.nekocloud.base.util;

public class JsonBuilder {

    private final StringBuilder json = new StringBuilder();

    public JsonBuilder addText(String text) {
        text = StringUtil.proccess(text);
        if (json.length() != 0)
            json.append(",");

        json.append("{\"text\":\"").append(text).append("\"}");
        return this;
    }

    public JsonBuilder addTextHover(String text, String hoverText) {
        text = StringUtil.proccess(text);
        if (json.length() != 0)
            json.append(",");

        json
                .append("{\"text\":\"")
                .append(text)
                .append("\", \"hoverEvent\":{\"action\":\"show_text\", \"value\": \"")
                .append(hoverText).append("\"}}");
        return this;
    }

    public JsonBuilder addRunCommand(String text, String command, String hoverText) {
        text = StringUtil.proccess(text);
        if (json.length() != 0)
            json.append(",");

        json
                .append("{\"text\":\"")
                .append(text)
                .append("\", \"hoverEvent\":{\"action\":\"show_text\", \"value\": \"")
                .append(hoverText).append("\"}, \"clickEvent\":{\"action\":\"run_command\", \"value\":\"")
                .append(command).append("\"}}");

        return this;
    }

    public JsonBuilder addOpenUrl(String text, String url, String hoverText) {
        text = StringUtil.proccess(text);
        if (json.length() != 0)
            json.append(",");

        json
                .append("{\"text\":\"").append(text).append("\", " +
                "\"hoverEvent\":{\"action\":\"show_text\", \"value\": \"").append(hoverText).append("\"}, " +
                "\"clickEvent\":{\"action\":\"open_url\", \"value\":\"").append(url).append("\"}" +
                "}");

        return this;
    }

    public JsonBuilder addSuggestCommand(String text, String command, String hoverText) {
        text = StringUtil.proccess(text);
        if (json.length() != 0)
            json.append(",");

        json.append("{\"text\":\"").append(text).append("\", \"hoverEvent\":{\"action\":\"show_text\", \"value\": \"")
             .append(hoverText).append("\"}, \"clickEvent\":{\"action\":\"suggest_command\", \"value\":\"")
             .append(command).append("\"}}");

        return this;
    }

    @Override
    public String toString() {
        return "[" + json + "]";
    }
}

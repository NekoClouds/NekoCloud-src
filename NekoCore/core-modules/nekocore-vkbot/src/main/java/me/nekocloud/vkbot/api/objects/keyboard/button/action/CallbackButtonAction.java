package me.nekocloud.vkbot.api.objects.keyboard.button.action;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.val;

@Getter
public class CallbackButtonAction extends KeyboardButtonAction{

	private final String label;

	public CallbackButtonAction(String payload, String label) {
		super(payload);

		this.label = label;
	}

	@Override
	protected JsonObject toJsonObject() {
		val params = new JsonObject();

		params.addProperty("type", "callback");
		params.addProperty("label", label);

		return params;
	}
}

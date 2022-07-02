package me.nekocloud.core.api.module;

import lombok.Data;

@Data
public class CoreModuleInfo {
	String main = "";
	String name = "";
	String[] authors = {};
	String version = "";
	String[] depends = {};
}

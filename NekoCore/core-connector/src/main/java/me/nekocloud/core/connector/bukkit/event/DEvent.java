package me.nekocloud.core.connector.bukkit.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class DEvent extends Event {

  private static final HandlerList HANDLER_LIST = new HandlerList();

  protected DEvent(boolean async) {
    super(async);
  }

  protected DEvent() {

  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }


  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

}

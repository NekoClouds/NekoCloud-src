package me.nekocloud.base.game;

public enum GameState {
    WAITING,  //устанавливается, когда идет ожидание игроков
    STARTING, //устанавливается, когда игра начинается
    GAME,     //устанавливается после начала игры
    END,      //устанавливается в конце игры
    RESTART   //устанавливается при перезапуске арены
}

package ru.glebova.NauJava.exception;

public class EmptyNameException extends IllegalArgumentException{
    public EmptyNameException() {
        super("Название предмета не может быть пустым");
    }
}

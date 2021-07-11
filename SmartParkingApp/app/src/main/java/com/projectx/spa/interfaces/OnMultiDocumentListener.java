package com.projectx.spa.interfaces;

public interface OnMultiDocumentListener {

    <T> void onAdded(T object);

    <T> void onModified(T object);

    <T> void onRemoved(T object);

}
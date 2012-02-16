/*
 * Copyright (c) 2012 Jared González
 * 
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of
 * the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * File:   org_java_ayatana_Collection.c
 * Author: Jared González
 */

#include "org_java_ayatana_Collections.h"
#include <stdlib.h>

ListIndex *collection_list_index_new() {
	ListIndex *list = (ListIndex *)malloc(sizeof(ListIndex));
	list->entries = (ListIndexEntry **)malloc(sizeof(ListIndexEntry *));
	list->allocated = 1;
	list->size = 0;
	return list;
}

void collection_list_index_add(ListIndex *list, long id, void *data) {
	if (list->size == list->allocated) {
		list->allocated *= 2;
		list->entries = (ListIndexEntry **)realloc(list->entries, sizeof(ListIndexEntry *)*list->allocated);
	}
	ListIndexEntry *entry = (ListIndexEntry *)malloc(sizeof(ListIndexEntry));
	entry->id = id;
	entry->data = data;
	list->entries[list->size] = entry;
	list->size++;
}

void *collection_list_index_get(ListIndex *list, long id) {
	int i;
	for (i=0;i<list->size;i++) {
		if (list->entries[i]->id == id)
			return list->entries[i]->data;
	}
	return NULL;
}

void *collection_list_index_remove(ListIndex *list, long id) {
	int i;
	for (i=0;i<list->size;i++) {
		if (list->entries[i]->id == id) {
			void *data = list->entries[i]->data;
			free(list->entries[i]);
			int j;
			for (j=i+1;j<list->size;j++) {
				list->entries[j-1] = list->entries[j];
			}
			list->size--;
			return data;
		}
	}
	return NULL;
}

void collection_list_index_destory(ListIndex *list) {
	int i;
	for (i=0;i<list->size;i++)
		free(list->entries[i]);
	free(list->entries);
	free(list);
}

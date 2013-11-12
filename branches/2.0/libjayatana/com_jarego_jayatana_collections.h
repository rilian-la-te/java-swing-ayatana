/*
 * com_jarego_jayatana_list_index.h
 *
 *  Created on: 10/11/2013
 *      Author: jared
 */

#ifndef COM_JAREGO_JAYATANA_LIST_INDEX_H_
#define COM_JAREGO_JAYATANA_LIST_INDEX_H_

typedef struct {
        long id;
        void *data;
} ListIndexEntry;

typedef struct {
        ListIndexEntry **entries;
        unsigned long allocated;
        unsigned long size;
} ListIndex;

ListIndex *collection_list_index_new();
void collection_list_index_add(ListIndex *, long, void *);
void *collection_list_index_get(ListIndex *, long);
void *collection_list_index_remove(ListIndex *, long);
void collection_list_index_destory(ListIndex *);
void collection_list_index_add_last(ListIndex *, void *);
void *collection_list_index_get_last(ListIndex *);
void *collection_list_index_remove_last(ListIndex *);

#endif /* COM_JAREGO_JAYATANA_LIST_INDEX_H_ */

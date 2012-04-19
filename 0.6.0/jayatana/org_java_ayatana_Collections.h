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
 * File:   org_java_ayatana_Collection.h
 * Author: Jared González
 */

#ifndef ORG_JAVA_AYATANA_COLLECTION_H
#define	ORG_JAVA_AYATANA_COLLECTION_H

#ifdef	__cplusplus
extern "C" {
#endif

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

#ifdef	__cplusplus
}
#endif

#endif	/* ORG_JAVA_AYATANA_COLLECTION_H */

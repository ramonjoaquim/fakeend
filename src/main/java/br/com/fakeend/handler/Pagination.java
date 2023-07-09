package br.com.fakeend.handler;

import br.com.fakeend.commons.RequestHandler;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Pagination<T> extends PagedModel<T> {

    private RequestHandler requestHandler;
    private PageRequest pageRequest;

    public PagedModel<T> pageOf(List<T> contents,
                                 int size,
                                 int page,
                                 long totalElements) {
        Page<T> resourcePage = new PageImpl<>(contents, pageRequest, totalElements);
        PagedModel.PageMetadata pageMetadata = getMetadata(resourcePage, size);
        PagedModel<T> pagedModel = PagedModel.of(contents, pageMetadata);
        appendLinks(pagedModel, resourcePage, page, size);

        return pagedModel;
    }

    private void appendLinks(PagedModel<T> pagedModel, Page<T> resourcePage, int page, int size) {
        if (resourcePage.hasNext()) {
            int nextPage = page + 1;
            Link nextLink = Link.of("%s?page=%d&size=%d".formatted(requestHandler.getRequestURL(), nextPage, size));
            pagedModel.add(nextLink);
        }

        if (resourcePage.hasPrevious()) {
            int prevPage = page - 1;
            Link prevLink = Link.of("%s?page=%d&size=%d".formatted(requestHandler.getRequestURL(), prevPage, size));
            pagedModel.add(prevLink);
        }
    }

    private PagedModel.PageMetadata getMetadata(Page<T> resourcePage, int size) {
        return new PagedModel.PageMetadata(
                size,
                resourcePage.getNumber(),
                resourcePage.getTotalElements()
        );
    }
}

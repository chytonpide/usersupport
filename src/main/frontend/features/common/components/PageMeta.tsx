import Head from 'next/head';

interface PageMetaProps {
    title:string;
}

export default function PageMeta(props: PageMetaProps) {

    const title = props.title + " | user-support"

    return <Head>
        <title>{title}</title>
    </Head>
}